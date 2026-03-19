package com.buyondip.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dip_alert")
public class DipAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(length = 100)
    private String companyName;

    @Column(precision = 12, scale = 2)
    private BigDecimal currentPrice;

    @Column(precision = 12, scale = 2)
    private BigDecimal peakPrice;

    @Column
    private LocalDate peakDate;

    @Column(precision = 8, scale = 2)
    private BigDecimal dipPercent;

    @Column(precision = 12, scale = 2)
    private BigDecimal troughPrice;

    @Column
    private LocalDate troughDate;

    @Column(precision = 8, scale = 2)
    private BigDecimal priorRisePercent;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DipCause cause;

    @Column(length = 300)
    private String causeMessage;

    @Column(nullable = false)
    private LocalDateTime detectedAt = LocalDateTime.now();

    @Column(nullable = false)
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private DipSource source = DipSource.WATCHLIST;

    @Column
    private Double compositeScore;

    @Column
    private Double fundamentalScore;

    public DipAlert() {}

    public Long getId() { return id; }
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
    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public DipSource getSource() { return source; }
    public void setSource(DipSource source) { this.source = source; }
    public Double getCompositeScore() { return compositeScore; }
    public void setCompositeScore(Double compositeScore) { this.compositeScore = compositeScore; }
    public Double getFundamentalScore() { return fundamentalScore; }
    public void setFundamentalScore(Double fundamentalScore) { this.fundamentalScore = fundamentalScore; }
}
