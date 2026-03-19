package com.buyondip.service;

import com.buyondip.dto.FundamentalsDto;
import org.springframework.stereotype.Service;

@Service
public class FundamentalsService {

    private final YahooFinanceService yahooFinanceService;

    public FundamentalsService(YahooFinanceService yahooFinanceService) {
        this.yahooFinanceService = yahooFinanceService;
    }

    public FundamentalsDto getFundamentals(String symbol) {
        return yahooFinanceService.getFundamentals(symbol);
    }
}
