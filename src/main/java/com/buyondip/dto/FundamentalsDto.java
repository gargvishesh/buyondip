package com.buyondip.dto;

import java.math.BigDecimal;

public class FundamentalsDto {
    private String symbol;
    private String companyName;
    private BigDecimal pe;
    private BigDecimal roe;
    private BigDecimal roce;
    private BigDecimal debtToEquity;
    private BigDecimal epsGrowth;
    private BigDecimal promoterHolding;
    private BigDecimal marketCap;
    private BigDecimal bookValue;
    private BigDecimal dividendYield;
    private String sector;

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public BigDecimal getPe() { return pe; }
    public void setPe(BigDecimal pe) { this.pe = pe; }
    public BigDecimal getRoe() { return roe; }
    public void setRoe(BigDecimal roe) { this.roe = roe; }
    public BigDecimal getRoce() { return roce; }
    public void setRoce(BigDecimal roce) { this.roce = roce; }
    public BigDecimal getDebtToEquity() { return debtToEquity; }
    public void setDebtToEquity(BigDecimal debtToEquity) { this.debtToEquity = debtToEquity; }
    public BigDecimal getEpsGrowth() { return epsGrowth; }
    public void setEpsGrowth(BigDecimal epsGrowth) { this.epsGrowth = epsGrowth; }
    public BigDecimal getPromoterHolding() { return promoterHolding; }
    public void setPromoterHolding(BigDecimal promoterHolding) { this.promoterHolding = promoterHolding; }
    public BigDecimal getMarketCap() { return marketCap; }
    public void setMarketCap(BigDecimal marketCap) { this.marketCap = marketCap; }
    public BigDecimal getBookValue() { return bookValue; }
    public void setBookValue(BigDecimal bookValue) { this.bookValue = bookValue; }
    public BigDecimal getDividendYield() { return dividendYield; }
    public void setDividendYield(BigDecimal dividendYield) { this.dividendYield = dividendYield; }
    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }
}
