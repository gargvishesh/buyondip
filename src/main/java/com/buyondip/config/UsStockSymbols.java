package com.buyondip.config;

import java.util.List;

public class UsStockSymbols {

    private UsStockSymbols() {}

    public static final List<String> SYMBOLS = List.of(
            // Technology
            "AAPL", "MSFT", "GOOGL", "AMZN", "META", "NVDA", "TSLA",
            "AVGO", "CRM", "ADBE", "NFLX", "INTC", "AMD", "QCOM", "TXN",
            "ORCL", "IBM", "CSCO", "ACN",
            // Financials
            "BRK-B", "JPM", "V", "MA", "BAC", "GS", "MS", "C", "WFC",
            // Healthcare
            "UNH", "JNJ", "MRK", "LLY", "ABBV", "TMO", "AMGN", "GILD",
            // Consumer
            "PG", "KO", "PEP", "MCD", "WMT", "COST", "HD", "DIS",
            // Energy
            "XOM", "CVX",
            // Industrials
            "GE", "CAT", "DE", "HON", "RTX", "BA"
    );
}
