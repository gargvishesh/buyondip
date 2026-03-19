package com.buyondip.controller;

import com.buyondip.dto.FundamentalsDto;
import com.buyondip.dto.PriceHistoryDto;
import com.buyondip.service.DipDetectionService;
import com.buyondip.service.FundamentalsService;
import com.buyondip.service.YahooFinanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final DipDetectionService dipDetectionService;
    private final FundamentalsService fundamentalsService;
    private final YahooFinanceService yahooFinanceService;

    public StockController(DipDetectionService dipDetectionService,
                           FundamentalsService fundamentalsService,
                           YahooFinanceService yahooFinanceService) {
        this.dipDetectionService = dipDetectionService;
        this.fundamentalsService = fundamentalsService;
        this.yahooFinanceService = yahooFinanceService;
    }

    @GetMapping("/{symbol}/price-history")
    public PriceHistoryDto getPriceHistory(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "6mo") String range) {
        return dipDetectionService.getPriceHistory(symbol.toUpperCase(), range);
    }

    @GetMapping("/{symbol}/fundamentals")
    public FundamentalsDto getFundamentals(@PathVariable String symbol) {
        return fundamentalsService.getFundamentals(symbol.toUpperCase());
    }

    @GetMapping("/search")
    public List<Map<String, String>> search(@RequestParam String q) {
        return yahooFinanceService.searchSymbols(q).stream()
                .map(arr -> Map.of("symbol", arr[0], "name", arr[1]))
                .toList();
    }
}
