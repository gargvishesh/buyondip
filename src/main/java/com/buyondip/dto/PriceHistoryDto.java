package com.buyondip.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PriceHistoryDto {
    private String symbol;
    private List<CandlestickDto> candles;
    private BigDecimal peakPrice;
    private LocalDate peakDate;
    private BigDecimal troughPrice;
    private LocalDate troughDate;
    private BigDecimal currentPrice;
    private BigDecimal dipPercent;
    private BigDecimal priorRisePercent;

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public List<CandlestickDto> getCandles() { return candles; }
    public void setCandles(List<CandlestickDto> candles) { this.candles = candles; }
    public BigDecimal getPeakPrice() { return peakPrice; }
    public void setPeakPrice(BigDecimal peakPrice) { this.peakPrice = peakPrice; }
    public LocalDate getPeakDate() { return peakDate; }
    public void setPeakDate(LocalDate peakDate) { this.peakDate = peakDate; }
    public BigDecimal getTroughPrice() { return troughPrice; }
    public void setTroughPrice(BigDecimal troughPrice) { this.troughPrice = troughPrice; }
    public LocalDate getTroughDate() { return troughDate; }
    public void setTroughDate(LocalDate troughDate) { this.troughDate = troughDate; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    public BigDecimal getDipPercent() { return dipPercent; }
    public void setDipPercent(BigDecimal dipPercent) { this.dipPercent = dipPercent; }
    public BigDecimal getPriorRisePercent() { return priorRisePercent; }
    public void setPriorRisePercent(BigDecimal priorRisePercent) { this.priorRisePercent = priorRisePercent; }
}
