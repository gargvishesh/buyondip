package com.buyondip.dto;

import java.math.BigDecimal;

public class StockSummaryDto {
    private String symbol;
    private String companyName;
    private String sector;
    private BigDecimal currentPrice;
    private BigDecimal change;
    private BigDecimal changePercent;
    private boolean inDip;
    private BigDecimal dipPercent;
    private String dipCause;
    private String exchange;

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    public BigDecimal getChange() { return change; }
    public void setChange(BigDecimal change) { this.change = change; }
    public BigDecimal getChangePercent() { return changePercent; }
    public void setChangePercent(BigDecimal changePercent) { this.changePercent = changePercent; }
    public boolean isInDip() { return inDip; }
    public void setInDip(boolean inDip) { this.inDip = inDip; }
    public BigDecimal getDipPercent() { return dipPercent; }
    public void setDipPercent(BigDecimal dipPercent) { this.dipPercent = dipPercent; }
    public String getDipCause() { return dipCause; }
    public void setDipCause(String dipCause) { this.dipCause = dipCause; }
    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }
}
