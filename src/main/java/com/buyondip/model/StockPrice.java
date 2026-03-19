package com.buyondip.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "stock_price", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"symbol", "priceDate"})
})
public class StockPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String symbol;

    @Column(nullable = false)
    private LocalDate priceDate;

    @Column(precision = 12, scale = 2)
    private BigDecimal open;

    @Column(precision = 12, scale = 2)
    private BigDecimal high;

    @Column(precision = 12, scale = 2)
    private BigDecimal low;

    @Column(precision = 12, scale = 2)
    private BigDecimal close;

    private Long volume;

    public StockPrice() {}

    public StockPrice(String symbol, LocalDate priceDate, BigDecimal open,
                      BigDecimal high, BigDecimal low, BigDecimal close, Long volume) {
        this.symbol = symbol;
        this.priceDate = priceDate;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }

    public Long getId() { return id; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public LocalDate getPriceDate() { return priceDate; }
    public void setPriceDate(LocalDate priceDate) { this.priceDate = priceDate; }
    public BigDecimal getOpen() { return open; }
    public void setOpen(BigDecimal open) { this.open = open; }
    public BigDecimal getHigh() { return high; }
    public void setHigh(BigDecimal high) { this.high = high; }
    public BigDecimal getLow() { return low; }
    public void setLow(BigDecimal low) { this.low = low; }
    public BigDecimal getClose() { return close; }
    public void setClose(BigDecimal close) { this.close = close; }
    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }
}
