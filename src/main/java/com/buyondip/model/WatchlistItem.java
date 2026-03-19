package com.buyondip.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "watchlist")
public class WatchlistItem {

    @Id
    @Column(nullable = false, unique = true, length = 20)
    private String symbol;

    @Column(nullable = false, length = 100)
    private String companyName;

    @Column(length = 50)
    private String sector;

    @Column(nullable = false)
    private LocalDateTime addedAt = LocalDateTime.now();

    public WatchlistItem() {}

    public WatchlistItem(String symbol, String companyName, String sector) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.sector = sector;
        this.addedAt = LocalDateTime.now();
    }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }
    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
}
