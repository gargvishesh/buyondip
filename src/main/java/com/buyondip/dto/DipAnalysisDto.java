package com.buyondip.dto;

import com.buyondip.model.DipCause;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DipAnalysisDto {
    private String symbol;
    private String companyName;
    private BigDecimal currentPrice;
    private BigDecimal peakPrice;
    private LocalDate peakDate;
    private BigDecimal dipPercent;
    private BigDecimal troughPrice;
    private LocalDate troughDate;
    private BigDecimal priorRisePercent;
    private DipCause cause;
    private String causeMessage;
    private String annotation;
    private LocalDateTime detectedAt;
    private String source;
    private Double compositeScore;
    private Double fundamentalScore;
    private String exchange;

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    public BigDecimal getPeakPrice() { return peakPrice; }
    public void setPeakPrice(BigDecimal peakPrice) { this.peakPrice = peakPrice; }
    public LocalDate getPeakDate() { return peakDate; }
    public void setPeakDate(LocalDate peakDate) { this.peakDate = peakDate; }
    public BigDecimal getDipPercent() { return dipPercent; }
    public void setDipPercent(BigDecimal dipPercent) { this.dipPercent = dipPercent; }
    public BigDecimal getTroughPrice() { return troughPrice; }
    public void setTroughPrice(BigDecimal troughPrice) { this.troughPrice = troughPrice; }
    public LocalDate getTroughDate() { return troughDate; }
    public void setTroughDate(LocalDate troughDate) { this.troughDate = troughDate; }
    public BigDecimal getPriorRisePercent() { return priorRisePercent; }
    public void setPriorRisePercent(BigDecimal priorRisePercent) { this.priorRisePercent = priorRisePercent; }
    public DipCause getCause() { return cause; }
    public void setCause(DipCause cause) { this.cause = cause; }
    public String getCauseMessage() { return causeMessage; }
    public void setCauseMessage(String causeMessage) { this.causeMessage = causeMessage; }
    public String getAnnotation() { return annotation; }
    public void setAnnotation(String annotation) { this.annotation = annotation; }
    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Double getCompositeScore() { return compositeScore; }
    public void setCompositeScore(Double compositeScore) { this.compositeScore = compositeScore; }
    public Double getFundamentalScore() { return fundamentalScore; }
    public void setFundamentalScore(Double fundamentalScore) { this.fundamentalScore = fundamentalScore; }
    public String getExchange() { return exchange; }
    public void setExchange(String exchange) { this.exchange = exchange; }
}
