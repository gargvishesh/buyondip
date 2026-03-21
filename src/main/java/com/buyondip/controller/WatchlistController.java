package com.buyondip.controller;

import com.buyondip.dto.StockSummaryDto;
import com.buyondip.model.WatchlistItem;
import com.buyondip.service.WatchlistService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping
    public List<StockSummaryDto> getWatchlist() {
        return watchlistService.getSummaries();
    }

    @PostMapping
    public ResponseEntity<WatchlistItem> addStock(@Valid @RequestBody AddStockRequest req) {
        WatchlistItem item = watchlistService.addStock(
                req.symbol().toUpperCase(),
                req.companyName(),
                req.sector(),
                req.exchange()
        );
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/{symbol}")
    public ResponseEntity<Void> removeStock(@PathVariable String symbol) {
        watchlistService.removeStock(symbol.toUpperCase());
        return ResponseEntity.noContent().build();
    }

    public record AddStockRequest(
            @NotBlank String symbol,
            @NotBlank String companyName,
            String sector,
            String exchange
    ) {}
}
