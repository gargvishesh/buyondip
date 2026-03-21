package com.buyondip.config;

import com.buyondip.model.WatchlistItem;
import com.buyondip.service.DipDetectionService;
import com.buyondip.service.WatchlistService;
import com.buyondip.service.YahooFinanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SchedulerConfig {

    private static final Logger log = LoggerFactory.getLogger(SchedulerConfig.class);

    private final WatchlistService watchlistService;
    private final YahooFinanceService yahooFinanceService;
    private final DipDetectionService dipDetectionService;

    public SchedulerConfig(WatchlistService watchlistService,
                           YahooFinanceService yahooFinanceService,
                           DipDetectionService dipDetectionService) {
        this.watchlistService = watchlistService;
        this.yahooFinanceService = yahooFinanceService;
        this.dipDetectionService = dipDetectionService;
    }

    @Scheduled(fixedRateString = "300000")
    public void refreshPrices() {
        List<WatchlistItem> items = watchlistService.getAllItems();
        if (items.isEmpty()) return;

        log.info("Scheduled price refresh for {} symbols", items.size());
        for (WatchlistItem item : items) {
            try {
                yahooFinanceService.refreshPriceHistory(item.getSymbol(), item.getExchange());
            } catch (Exception e) {
                log.warn("Failed to refresh prices for {}: {}", item.getSymbol(), e.getMessage());
            }
        }

        try {
            int count = dipDetectionService.detectAllDips();
            log.info("Dip detection complete: {} alerts", count);
        } catch (Exception e) {
            log.warn("Dip detection failed: {}", e.getMessage());
        }
    }

    @Scheduled(fixedRate = 3600000)
    public void refreshMarketDips() {
        log.info("Starting hourly market dip scan for Nifty 500");
        try {
            int count = dipDetectionService.detectMarketDips(25);
            log.info("Market dip scan complete: {} top alerts", count);
        } catch (Exception e) {
            log.warn("Market dip scan failed: {}", e.getMessage());
        }
    }
}
