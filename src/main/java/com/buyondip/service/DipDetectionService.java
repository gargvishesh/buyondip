package com.buyondip.service;

import com.buyondip.config.Nifty500Symbols;
import com.buyondip.config.UsStockSymbols;
import com.buyondip.dto.CandlestickDto;
import com.buyondip.dto.DipAnalysisDto;
import com.buyondip.dto.FundamentalsDto;
import com.buyondip.dto.PriceHistoryDto;
import com.buyondip.model.DipAlert;
import com.buyondip.model.DipSource;
import com.buyondip.model.StockPrice;
import com.buyondip.model.WatchlistItem;
import com.buyondip.repository.DipAlertRepository;
import com.buyondip.repository.StockPriceRepository;
import com.buyondip.repository.WatchlistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class DipDetectionService {

    private static final Logger log = LoggerFactory.getLogger(DipDetectionService.class);

    private static final double DIP_THRESHOLD = -10.0;
    private static final double RISE_THRESHOLD = 15.0;
    private static final int LOOKBACK_DAYS = 90;

    private final WatchlistRepository watchlistRepository;
    private final StockPriceRepository stockPriceRepository;
    private final DipAlertRepository dipAlertRepository;
    private final DipCauseService dipCauseService;
    private final YahooFinanceService yahooFinanceService;
    private final FundamentalScoringService fundamentalScoringService;

    public DipDetectionService(WatchlistRepository watchlistRepository,
                               StockPriceRepository stockPriceRepository,
                               DipAlertRepository dipAlertRepository,
                               DipCauseService dipCauseService,
                               YahooFinanceService yahooFinanceService,
                               FundamentalScoringService fundamentalScoringService) {
        this.watchlistRepository = watchlistRepository;
        this.stockPriceRepository = stockPriceRepository;
        this.dipAlertRepository = dipAlertRepository;
        this.dipCauseService = dipCauseService;
        this.yahooFinanceService = yahooFinanceService;
        this.fundamentalScoringService = fundamentalScoringService;
    }

    private record DipData(
            String symbol,
            BigDecimal currentPrice,
            BigDecimal peakPrice,
            LocalDate peakDate,
            BigDecimal dipPercent,
            BigDecimal troughPrice,
            LocalDate troughDate,
            BigDecimal priorRisePercent
    ) {}

    /**
     * Computes dip data for a symbol using cached price history.
     * Does NOT save anything to the database.
     */
    private Optional<DipData> computeDipData(String symbol) {
        LocalDate since = LocalDate.now().minusDays(LOOKBACK_DAYS);
        List<StockPrice> prices = stockPriceRepository
                .findBySymbolAndPriceDateBetweenOrderByPriceDateAsc(symbol, since, LocalDate.now());

        if (prices.size() < 10) {
            log.debug("Not enough data for {} ({} records)", symbol, prices.size());
            return Optional.empty();
        }

        StockPrice peakEntry = prices.stream()
                .max(Comparator.comparing(StockPrice::getClose))
                .orElseThrow();
        BigDecimal peakPrice = peakEntry.getClose();
        LocalDate peakDate = peakEntry.getPriceDate();

        StockPrice latest = prices.get(prices.size() - 1);
        BigDecimal currentPrice = latest.getClose();

        BigDecimal dipPercent = currentPrice.subtract(peakPrice)
                .divide(peakPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

        if (dipPercent.doubleValue() > DIP_THRESHOLD) {
            return Optional.empty();
        }

        List<StockPrice> beforePeak = prices.stream()
                .filter(p -> !p.getPriceDate().isAfter(peakDate))
                .toList();

        if (beforePeak.isEmpty()) return Optional.empty();

        StockPrice troughEntry = beforePeak.stream()
                .min(Comparator.comparing(StockPrice::getClose))
                .orElseThrow();
        BigDecimal troughPrice = troughEntry.getClose();
        LocalDate troughDate = troughEntry.getPriceDate();

        BigDecimal priorRisePercent = peakPrice.subtract(troughPrice)
                .divide(troughPrice, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

        if (priorRisePercent.doubleValue() < RISE_THRESHOLD) {
            log.debug("{}: prior rise only {}%, below threshold", symbol, priorRisePercent);
            return Optional.empty();
        }

        return Optional.of(new DipData(symbol, currentPrice, peakPrice, peakDate,
                dipPercent, troughPrice, troughDate, priorRisePercent));
    }

    @Transactional
    public int detectAllDips() {
        List<WatchlistItem> items = watchlistRepository.findAll();
        int count = 0;
        for (WatchlistItem item : items) {
            try {
                Optional<DipAnalysisDto> result = analyzeStock(item.getSymbol(), item.getSector(), DipSource.WATCHLIST, item.getExchange());
                if (result.isPresent()) count++;
            } catch (Exception e) {
                log.warn("Dip detection failed for {}: {}", item.getSymbol(), e.getMessage());
            }
        }
        return count;
    }

    @Transactional
    public Optional<DipAnalysisDto> analyzeStock(String symbol, String sector, DipSource source) {
        return analyzeStock(symbol, sector, source, "NSE");
    }

    @Transactional
    public Optional<DipAnalysisDto> analyzeStock(String symbol, String sector, DipSource source, String exchange) {
        Optional<DipData> dipDataOpt = computeDipData(symbol);

        if (dipDataOpt.isEmpty()) {
            // Deactivate any existing alert if no longer dipping
            dipAlertRepository.findTopBySymbolAndActiveAndSourceOrderByDetectedAtDesc(symbol, true, source)
                    .ifPresent(a -> {
                        a.setActive(false);
                        dipAlertRepository.save(a);
                    });
            return Optional.empty();
        }

        DipData d = dipDataOpt.get();
        DipCauseService.CauseResult causeResult = dipCauseService.detectCause(sector, d.peakDate(), exchange);

        DipAlert alert = new DipAlert();
        alert.setSymbol(symbol);
        WatchlistItem item = watchlistRepository.findById(symbol).orElse(null);
        if (item != null) alert.setCompanyName(item.getCompanyName());
        alert.setCurrentPrice(d.currentPrice());
        alert.setPeakPrice(d.peakPrice());
        alert.setPeakDate(d.peakDate());
        alert.setDipPercent(d.dipPercent());
        alert.setTroughPrice(d.troughPrice());
        alert.setTroughDate(d.troughDate());
        alert.setPriorRisePercent(d.priorRisePercent());
        alert.setCause(causeResult.cause());
        alert.setCauseMessage(causeResult.message());
        alert.setActive(true);
        alert.setSource(source);
        alert.setExchange(exchange);

        dipAlertRepository.findTopBySymbolAndActiveAndSourceOrderByDetectedAtDesc(symbol, true, source)
                .ifPresent(old -> {
                    old.setActive(false);
                    dipAlertRepository.save(old);
                });

        dipAlertRepository.save(alert);
        return Optional.of(buildDto(alert));
    }

    /**
     * Scans all Nifty 500 + US symbols for dips, scores them by composite score
     * (30% dip depth + 70% fundamental quality), and saves the top N as MARKET alerts.
     */
    @Transactional
    public int detectMarketDips(int topN) {
        record SymbolWithExchange(String symbol, String exchange) {}

        List<SymbolWithExchange> allSymbols = new ArrayList<>();
        for (String s : Nifty500Symbols.SYMBOLS) allSymbols.add(new SymbolWithExchange(s, "NSE"));
        for (String s : UsStockSymbols.SYMBOLS) allSymbols.add(new SymbolWithExchange(s, "NYSE"));

        List<DipData> candidates = new ArrayList<>();
        java.util.Map<String, String> symbolExchangeMap = new java.util.HashMap<>();

        for (SymbolWithExchange se : allSymbols) {
            try {
                yahooFinanceService.getPriceHistory(se.symbol(), LOOKBACK_DAYS);
                computeDipData(se.symbol()).ifPresent(d -> {
                    candidates.add(d);
                    symbolExchangeMap.put(d.symbol(), se.exchange());
                });
            } catch (Exception e) {
                log.debug("Market scan skipped {}: {}", se.symbol(), e.getMessage());
            }
        }

        log.info("Market scan: {} dip candidates from Nifty 500 + US", candidates.size());

        record ScoredCandidate(DipData dip, double dipScore, double fundamentalScore, double compositeScore,
                               String companyName, String sector, String exchange) {}

        List<ScoredCandidate> scored = new ArrayList<>();
        for (DipData d : candidates) {
            try {
                String exchange = symbolExchangeMap.getOrDefault(d.symbol(), "NSE");
                double dipScore = Math.min(1.0, Math.max(0.0,
                        (Math.abs(d.dipPercent().doubleValue()) - 10.0) / 40.0));

                FundamentalsDto fundamentals = yahooFinanceService.getFundamentals(d.symbol(), exchange);
                double fundScore = fundamentalScoringService.score(fundamentals, exchange);
                double composite = 0.3 * dipScore + 0.7 * fundScore;

                scored.add(new ScoredCandidate(d, dipScore, fundScore, composite,
                        fundamentals.getCompanyName(), fundamentals.getSector(), exchange));
            } catch (Exception e) {
                log.debug("Scoring failed for {}: {}", d.symbol(), e.getMessage());
            }
        }

        // Sort by composite score descending, take top N
        scored.sort(Comparator.comparingDouble(ScoredCandidate::compositeScore).reversed());
        List<ScoredCandidate> topCandidates = scored.stream().limit(topN).toList();

        // Deactivate old MARKET alerts
        dipAlertRepository.findBySource(DipSource.MARKET).forEach(a -> {
            a.setActive(false);
            dipAlertRepository.save(a);
        });

        // Save new MARKET alerts
        for (ScoredCandidate sc : topCandidates) {
            try {
                DipData d = sc.dip();
                DipCauseService.CauseResult causeResult = dipCauseService.detectCause(sc.sector(), d.peakDate(), sc.exchange());

                DipAlert alert = new DipAlert();
                alert.setSymbol(d.symbol());
                alert.setCompanyName(sc.companyName());
                alert.setCurrentPrice(d.currentPrice());
                alert.setPeakPrice(d.peakPrice());
                alert.setPeakDate(d.peakDate());
                alert.setDipPercent(d.dipPercent());
                alert.setTroughPrice(d.troughPrice());
                alert.setTroughDate(d.troughDate());
                alert.setPriorRisePercent(d.priorRisePercent());
                alert.setCause(causeResult.cause());
                alert.setCauseMessage(causeResult.message());
                alert.setActive(true);
                alert.setSource(DipSource.MARKET);
                alert.setCompositeScore(sc.compositeScore());
                alert.setFundamentalScore(sc.fundamentalScore());
                alert.setExchange(sc.exchange());

                dipAlertRepository.save(alert);
            } catch (Exception e) {
                log.warn("Failed to save market alert for {}: {}", sc.dip().symbol(), e.getMessage());
            }
        }

        log.info("Market scan saved {} top composite-scored alerts", topCandidates.size());
        return topCandidates.size();
    }

    public List<DipAnalysisDto> getActiveAlerts() {
        return dipAlertRepository.findByActiveAndSourceOrderByDipPercentAsc(true, DipSource.WATCHLIST)
                .stream()
                .map(this::buildDto)
                .toList();
    }

    public List<DipAnalysisDto> getMarketAlerts() {
        return dipAlertRepository.findByActiveAndSourceOrderByCompositeScoreDesc(true, DipSource.MARKET)
                .stream()
                .map(this::buildDto)
                .toList();
    }

    public Optional<DipAnalysisDto> getAlertForSymbol(String symbol) {
        return dipAlertRepository.findTopBySymbolAndActiveOrderByDetectedAtDesc(symbol, true)
                .map(this::buildDto);
    }

    public PriceHistoryDto getPriceHistory(String symbol, String range) {
        int days = parseDays(range);
        List<StockPrice> prices = yahooFinanceService.getPriceHistory(symbol, days);

        PriceHistoryDto dto = new PriceHistoryDto();
        dto.setSymbol(symbol);

        if (prices.isEmpty()) {
            dto.setCandles(List.of());
            return dto;
        }

        dto.setCandles(prices.stream().map(p -> {
            CandlestickDto c = new CandlestickDto();
            c.setDate(p.getPriceDate());
            c.setOpen(p.getOpen());
            c.setHigh(p.getHigh());
            c.setLow(p.getLow());
            c.setClose(p.getClose());
            c.setVolume(p.getVolume());
            return c;
        }).toList());

        StockPrice peak = prices.stream()
                .max(Comparator.comparing(StockPrice::getClose))
                .orElse(prices.get(prices.size() - 1));
        StockPrice latestPrice = prices.get(prices.size() - 1);
        List<StockPrice> beforePeak = prices.stream()
                .filter(p -> !p.getPriceDate().isAfter(peak.getPriceDate()))
                .toList();
        StockPrice trough = beforePeak.isEmpty() ? prices.get(0) :
                beforePeak.stream().min(Comparator.comparing(StockPrice::getClose)).orElse(beforePeak.get(0));

        dto.setPeakPrice(peak.getClose());
        dto.setPeakDate(peak.getPriceDate());
        dto.setTroughPrice(trough.getClose());
        dto.setTroughDate(trough.getPriceDate());
        dto.setCurrentPrice(latestPrice.getClose());

        if (peak.getClose().compareTo(BigDecimal.ZERO) > 0) {
            dto.setDipPercent(latestPrice.getClose().subtract(peak.getClose())
                    .divide(peak.getClose(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP));
        }
        if (trough.getClose().compareTo(BigDecimal.ZERO) > 0) {
            dto.setPriorRisePercent(peak.getClose().subtract(trough.getClose())
                    .divide(trough.getClose(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP));
        }

        return dto;
    }

    private DipAnalysisDto buildDto(DipAlert a) {
        DipAnalysisDto dto = new DipAnalysisDto();
        dto.setSymbol(a.getSymbol());
        dto.setCompanyName(a.getCompanyName());
        dto.setCurrentPrice(a.getCurrentPrice());
        dto.setPeakPrice(a.getPeakPrice());
        dto.setPeakDate(a.getPeakDate());
        dto.setDipPercent(a.getDipPercent());
        dto.setTroughPrice(a.getTroughPrice());
        dto.setTroughDate(a.getTroughDate());
        dto.setPriorRisePercent(a.getPriorRisePercent());
        dto.setCause(a.getCause());
        dto.setCauseMessage(a.getCauseMessage());
        dto.setDetectedAt(a.getDetectedAt());
        dto.setAnnotation(buildAnnotation(a));
        dto.setSource(a.getSource() != null ? a.getSource().name() : DipSource.WATCHLIST.name());
        dto.setCompositeScore(a.getCompositeScore());
        dto.setFundamentalScore(a.getFundamentalScore());
        dto.setExchange(a.getExchange());
        return dto;
    }

    private String buildAnnotation(DipAlert a) {
        if (a.getTroughDate() == null || a.getPeakDate() == null) return "";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMM yyyy");
        String troughStr = a.getTroughDate().format(fmt);
        String peakStr = a.getPeakDate().format(fmt);
        boolean isUs = "NYSE".equals(a.getExchange()) || "NASDAQ".equals(a.getExchange());
        String ccy = isUs ? "$" : "₹";
        String peakPrice = a.getPeakPrice() != null
                ? ccy + String.format("%,.0f", a.getPeakPrice().doubleValue()) : "";
        double rise = a.getPriorRisePercent() != null ? a.getPriorRisePercent().doubleValue() : 0.0;
        double dip = a.getDipPercent() != null ? Math.abs(a.getDipPercent().doubleValue()) : 0.0;
        return String.format("Rose %.1f%% from %s → %s, now down %.1f%% from peak of %s",
                rise, troughStr, peakStr, dip, peakPrice);
    }

    private int parseDays(String range) {
        if (range == null) return 180;
        return switch (range) {
            case "1mo" -> 30;
            case "3mo" -> 90;
            case "6mo" -> 180;
            case "1y" -> 365;
            case "2y" -> 730;
            default -> 180;
        };
    }
}
