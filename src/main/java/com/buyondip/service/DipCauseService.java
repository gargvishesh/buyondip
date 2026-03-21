package com.buyondip.service;

import com.buyondip.model.DipCause;
import com.buyondip.model.StockPrice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DipCauseService {

    private static final Logger log = LoggerFactory.getLogger(DipCauseService.class);

    private final YahooFinanceService yahooFinanceService;

    public DipCauseService(YahooFinanceService yahooFinanceService) {
        this.yahooFinanceService = yahooFinanceService;
    }

    private static final Map<String, String> SECTOR_INDEX_MAP = Map.of(
            "IT", "^CNXIT",
            "Bank", "^NSEBANK",
            "Pharma", "^CNXPHARMA",
            "Auto", "^CNXAUTO",
            "Energy", "^CNXENERGY",
            "FMCG", "^CNXFMCG",
            "Metal", "^CNXMETAL",
            "Realty", "^CNXREALTY"
    );

    private static final Map<String, String> US_SECTOR_INDEX_MAP = Map.of(
            "Technology", "XLK",
            "Financials", "XLF",
            "Healthcare", "XLV",
            "Energy", "XLE",
            "Consumer Discretionary", "XLY",
            "Consumer Staples", "XLP",
            "Industrials", "XLI",
            "Utilities", "XLU"
    );

    private static final double GLOBAL_THRESHOLD = -3.0;
    private static final double SECTOR_THRESHOLD = -3.0;

    public record CauseResult(DipCause cause, String message) {}

    public CauseResult detectCause(String sector, LocalDate peakDate) {
        return detectCause(sector, peakDate, "NSE");
    }

    public CauseResult detectCause(String sector, LocalDate peakDate, String exchange) {
        boolean isUs = "NYSE".equals(exchange) || "NASDAQ".equals(exchange);
        String broadIndex = isUs ? "^GSPC" : "^NSEI";
        String broadName = isUs ? "S&P 500" : "Nifty 50";

        double broadDrop = computeDrop(broadIndex, peakDate);
        if (broadDrop <= GLOBAL_THRESHOLD) {
            return new CauseResult(
                    DipCause.GLOBAL,
                    String.format("%s also fell %.1f%% — broad market correction", broadName, broadDrop)
            );
        }

        if (sector != null) {
            String sectorIndex = isUs ? findUsSectorIndex(sector) : findSectorIndex(sector);
            if (sectorIndex != null) {
                double sectorDrop = computeDrop(sectorIndex, peakDate);
                if (sectorDrop <= SECTOR_THRESHOLD) {
                    return new CauseResult(
                            DipCause.SECTOR,
                            String.format("%s sector fell %.1f%% — sector rotation", sector, sectorDrop)
                    );
                }
            }
        }

        return new CauseResult(
                DipCause.COMPANY_SPECIFIC,
                "Company-specific — investigate before buying"
        );
    }

    private double computeDrop(String indexSymbol, LocalDate peakDate) {
        try {
            List<StockPrice> prices = yahooFinanceService.getPriceHistory(indexSymbol, 120);
            if (prices.isEmpty()) return 0.0;

            Optional<StockPrice> peakEntry = prices.stream()
                    .filter(p -> !p.getPriceDate().isAfter(peakDate))
                    .reduce((a, b) -> b);

            Optional<StockPrice> latest = prices.stream().reduce((a, b) -> b);

            if (peakEntry.isEmpty() || latest.isEmpty()) return 0.0;

            BigDecimal peakClose = peakEntry.get().getClose();
            BigDecimal latestClose = latest.get().getClose();

            if (peakClose.compareTo(BigDecimal.ZERO) == 0) return 0.0;

            return latestClose.subtract(peakClose)
                    .divide(peakClose, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        } catch (Exception e) {
            log.warn("Failed to compute drop for {}: {}", indexSymbol, e.getMessage());
            return 0.0;
        }
    }

    private String findSectorIndex(String sector) {
        for (Map.Entry<String, String> entry : SECTOR_INDEX_MAP.entrySet()) {
            if (sector.toLowerCase().contains(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private String findUsSectorIndex(String sector) {
        for (Map.Entry<String, String> entry : US_SECTOR_INDEX_MAP.entrySet()) {
            if (sector.toLowerCase().contains(entry.getKey().toLowerCase())) {
                return entry.getValue();
            }
        }
        return null;
    }
}
