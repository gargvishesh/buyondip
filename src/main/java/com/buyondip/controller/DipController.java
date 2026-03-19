package com.buyondip.controller;

import com.buyondip.dto.DipAnalysisDto;
import com.buyondip.service.DipDetectionService;
import com.buyondip.service.WatchlistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dips")
public class DipController {

    private final DipDetectionService dipDetectionService;
    private final WatchlistService watchlistService;

    public DipController(DipDetectionService dipDetectionService, WatchlistService watchlistService) {
        this.dipDetectionService = dipDetectionService;
        this.watchlistService = watchlistService;
    }

    @GetMapping
    public List<DipAnalysisDto> getActiveDips() {
        return dipDetectionService.getActiveAlerts();
    }

    @GetMapping("/market")
    public List<DipAnalysisDto> getMarketDips() {
        return dipDetectionService.getMarketAlerts();
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<DipAnalysisDto> getDip(@PathVariable String symbol) {
        return dipDetectionService.getAlertForSymbol(symbol.toUpperCase())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/refresh")
    public Map<String, Object> refresh() {
        watchlistService.getAllItems().forEach(item -> {
            try {
                // Price refresh is handled by the detection service via YahooFinanceService
            } catch (Exception ignored) {}
        });
        int count = dipDetectionService.detectAllDips();
        return Map.of("alertsDetected", count, "status", "ok");
    }

    @PostMapping("/market/refresh")
    public Map<String, Object> refreshMarket() {
        int count = dipDetectionService.detectMarketDips(25);
        return Map.of("alertsDetected", count, "status", "ok");
    }
}
