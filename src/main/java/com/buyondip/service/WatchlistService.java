package com.buyondip.service;

import com.buyondip.dto.StockSummaryDto;
import com.buyondip.model.DipAlert;
import com.buyondip.model.WatchlistItem;
import com.buyondip.repository.DipAlertRepository;
import com.buyondip.repository.WatchlistRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WatchlistService {

    private static final Logger log = LoggerFactory.getLogger(WatchlistService.class);

    private final WatchlistRepository watchlistRepository;
    private final DipAlertRepository dipAlertRepository;
    private final YahooFinanceService yahooFinanceService;

    public WatchlistService(WatchlistRepository watchlistRepository,
                            DipAlertRepository dipAlertRepository,
                            YahooFinanceService yahooFinanceService) {
        this.watchlistRepository = watchlistRepository;
        this.dipAlertRepository = dipAlertRepository;
        this.yahooFinanceService = yahooFinanceService;
    }

    public List<WatchlistItem> getAllItems() {
        return watchlistRepository.findAll();
    }

    @Transactional
    public WatchlistItem addStock(String symbol, String companyName, String sector) {
        return addStock(symbol, companyName, sector, "NSE");
    }

    @Transactional
    public WatchlistItem addStock(String symbol, String companyName, String sector, String exchange) {
        if (watchlistRepository.existsById(symbol)) {
            throw new IllegalArgumentException("Symbol " + symbol + " already in watchlist");
        }
        String resolvedExchange = exchange != null ? exchange : "NSE";
        WatchlistItem item = new WatchlistItem(symbol, companyName, sector, resolvedExchange);
        WatchlistItem saved = watchlistRepository.save(item);
        yahooFinanceService.refreshPriceHistory(symbol, resolvedExchange);
        return saved;
    }

    @Transactional
    public void removeStock(String symbol) {
        watchlistRepository.deleteById(symbol);
        dipAlertRepository.deleteBySymbol(symbol);
    }

    public List<StockSummaryDto> getSummaries() {
        List<WatchlistItem> items = watchlistRepository.findAll();
        List<StockSummaryDto> summaries = new ArrayList<>();

        for (WatchlistItem item : items) {
            StockSummaryDto dto = new StockSummaryDto();
            dto.setSymbol(item.getSymbol());
            dto.setCompanyName(item.getCompanyName());
            dto.setSector(item.getSector());
            dto.setExchange(item.getExchange());

            Optional<BigDecimal[]> quote = yahooFinanceService.getCurrentQuote(item.getSymbol(), item.getExchange());
            quote.ifPresent(q -> {
                dto.setCurrentPrice(q[0]);
                dto.setChange(q[1]);
                dto.setChangePercent(q[2]);
            });

            Optional<DipAlert> alert = dipAlertRepository
                    .findTopBySymbolAndActiveOrderByDetectedAtDesc(item.getSymbol(), true);
            alert.ifPresent(a -> {
                dto.setInDip(true);
                dto.setDipPercent(a.getDipPercent());
                dto.setDipCause(a.getCause() != null ? a.getCause().name() : null);
            });

            summaries.add(dto);
        }
        return summaries;
    }
}
