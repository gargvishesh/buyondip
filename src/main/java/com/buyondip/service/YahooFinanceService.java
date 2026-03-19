package com.buyondip.service;

import com.buyondip.dto.FundamentalsDto;
import com.buyondip.model.StockPrice;
import com.buyondip.repository.StockPriceRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class YahooFinanceService {

    private static final Logger log = LoggerFactory.getLogger(YahooFinanceService.class);

    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final StockPriceRepository stockPriceRepository;

    private static final String YAHOO_BASE = "https://query1.finance.yahoo.com";
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");

    public YahooFinanceService(OkHttpClient okHttpClient, ObjectMapper objectMapper,
                               StockPriceRepository stockPriceRepository) {
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
        this.stockPriceRepository = stockPriceRepository;
    }

    public String toYahooSymbol(String symbol) {
        if (symbol.startsWith("^") || symbol.contains(".")) {
            return symbol;
        }
        return symbol + ".NS";
    }

    @Transactional
    public List<StockPrice> refreshPriceHistory(String symbol) {
        String yahooSymbol = toYahooSymbol(symbol);
        String url = YAHOO_BASE + "/v8/finance/chart/" + yahooSymbol
                + "?range=3mo&interval=1d&includePrePost=false";

        try {
            JsonNode root = fetchJson(url);
            JsonNode result = root.path("chart").path("result").get(0);
            if (result == null || result.isMissingNode()) {
                log.warn("No chart result for {}", symbol);
                return List.of();
            }

            JsonNode timestamps = result.path("timestamp");
            JsonNode quote = result.path("indicators").path("quote").get(0);
            if (quote == null) return List.of();

            JsonNode opens = quote.path("open");
            JsonNode highs = quote.path("high");
            JsonNode lows = quote.path("low");
            JsonNode closes = quote.path("close");
            JsonNode volumes = quote.path("volume");

            List<StockPrice> prices = new ArrayList<>();
            for (int i = 0; i < timestamps.size(); i++) {
                long ts = timestamps.get(i).asLong();
                LocalDate date = Instant.ofEpochSecond(ts).atZone(IST).toLocalDate();

                if (closes.get(i).isNull()) continue;

                BigDecimal open = safeDecimal(opens.get(i));
                BigDecimal high = safeDecimal(highs.get(i));
                BigDecimal low = safeDecimal(lows.get(i));
                BigDecimal close = safeDecimal(closes.get(i));
                Long volume = volumes.get(i).isNull() ? 0L : volumes.get(i).asLong();

                prices.add(new StockPrice(symbol, date, open, high, low, close, volume));
            }

            LocalDate minDate = prices.stream().map(StockPrice::getPriceDate)
                    .min(LocalDate::compareTo).orElse(LocalDate.now().minusMonths(3));
            List<StockPrice> existing = stockPriceRepository
                    .findBySymbolAndPriceDateBetweenOrderByPriceDateAsc(symbol, minDate, LocalDate.now());
            stockPriceRepository.deleteAll(existing);
            stockPriceRepository.saveAll(prices);
            log.info("Refreshed {} price records for {}", prices.size(), symbol);
            return prices;

        } catch (Exception e) {
            log.error("Failed to fetch price history for {}: {}", symbol, e.getMessage());
            return List.of();
        }
    }

    public Optional<BigDecimal[]> getCurrentQuote(String symbol) {
        String yahooSymbol = toYahooSymbol(symbol);
        String url = YAHOO_BASE + "/v8/finance/chart/" + yahooSymbol
                + "?range=1d&interval=1d&includePrePost=false";

        try {
            JsonNode root = fetchJson(url);
            JsonNode meta = root.path("chart").path("result").get(0).path("meta");
            BigDecimal price = safeDecimal(meta.path("regularMarketPrice"));
            BigDecimal prev = safeDecimal(meta.path("chartPreviousClose"));
            BigDecimal change = price.subtract(prev);
            BigDecimal changePct = prev.compareTo(BigDecimal.ZERO) != 0
                    ? change.divide(prev, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                    : BigDecimal.ZERO;
            return Optional.of(new BigDecimal[]{price, change, changePct});
        } catch (Exception e) {
            log.warn("Failed to get quote for {}: {}", symbol, e.getMessage());
            return Optional.empty();
        }
    }

    @Cacheable(value = "fundamentals", key = "#symbol")
    public FundamentalsDto getFundamentals(String symbol) {
        String yahooSymbol = toYahooSymbol(symbol);
        String url = YAHOO_BASE + "/v10/finance/quoteSummary/" + yahooSymbol
                + "?modules=summaryDetail,defaultKeyStatistics,financialData,assetProfile";

        FundamentalsDto dto = new FundamentalsDto();
        dto.setSymbol(symbol);

        try {
            JsonNode root = fetchJson(url);
            JsonNode result = root.path("quoteSummary").path("result").get(0);

            JsonNode summary = result.path("summaryDetail");
            JsonNode stats = result.path("defaultKeyStatistics");
            JsonNode financial = result.path("financialData");
            JsonNode profile = result.path("assetProfile");

            String bio = profile.path("longBusinessSummary").asText("");
            dto.setCompanyName(bio.length() > 60 ? bio.substring(0, 60) : bio);
            dto.setSector(profile.path("sector").asText("Unknown"));

            dto.setPe(safeDecimalNode(summary.path("trailingPE").path("raw")));
            dto.setMarketCap(safeDecimalNode(summary.path("marketCap").path("raw")));
            dto.setDividendYield(safeDecimalNode(summary.path("dividendYield").path("raw")));
            dto.setBookValue(safeDecimalNode(stats.path("bookValue").path("raw")));
            dto.setRoe(safeDecimalNode(financial.path("returnOnEquity").path("raw")));
            dto.setDebtToEquity(safeDecimalNode(financial.path("debtToEquity").path("raw")));
            dto.setEpsGrowth(safeDecimalNode(stats.path("earningsGrowth").path("raw")));

        } catch (Exception e) {
            log.warn("Partial fundamentals for {}: {}", symbol, e.getMessage());
        }

        return dto;
    }

    @Cacheable(value = "symbolSearch", key = "#query")
    public List<String[]> searchSymbols(String query) {
        String url = "https://query2.finance.yahoo.com/v1/finance/search?q=" + query
                + "&lang=en-IN&region=IN&quotesCount=8&newsCount=0&listsCount=0";

        List<String[]> results = new ArrayList<>();
        try {
            JsonNode root = fetchJson(url);
            JsonNode quotes = root.path("quotes");
            for (JsonNode q : quotes) {
                String sym = q.path("symbol").asText();
                String name = q.path("longname").asText(q.path("shortname").asText(""));
                String type = q.path("quoteType").asText("");
                if ("EQUITY".equals(type) && sym.endsWith(".NS")) {
                    results.add(new String[]{sym.replace(".NS", ""), name});
                }
            }
        } catch (Exception e) {
            log.warn("Search failed for {}: {}", query, e.getMessage());
        }
        return results;
    }

    public List<StockPrice> getPriceHistory(String symbol, int days) {
        LocalDate since = LocalDate.now().minusDays(days);
        List<StockPrice> cached = stockPriceRepository
                .findBySymbolAndPriceDateBetweenOrderByPriceDateAsc(symbol, since, LocalDate.now());

        if (cached.isEmpty()) {
            return refreshPriceHistory(symbol).stream()
                    .filter(p -> !p.getPriceDate().isBefore(since))
                    .toList();
        }
        return cached;
    }

    private JsonNode fetchJson(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0")
                .header("Accept", "application/json")
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP " + response.code() + " for " + url);
            }
            return objectMapper.readTree(response.body().string());
        }
    }

    private BigDecimal safeDecimal(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) return BigDecimal.ZERO;
        return BigDecimal.valueOf(node.asDouble()).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal safeDecimalNode(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) return null;
        double val = node.asDouble();
        if (val == 0.0 && node.asText().isEmpty()) return null;
        return BigDecimal.valueOf(val).setScale(4, RoundingMode.HALF_UP);
    }
}
