package com.buyondip.repository;

import com.buyondip.model.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {

    List<StockPrice> findBySymbolAndPriceDateBetweenOrderByPriceDateAsc(
            String symbol, LocalDate start, LocalDate end);

    List<StockPrice> findBySymbolOrderByPriceDateAsc(String symbol);

    Optional<StockPrice> findTopBySymbolOrderByPriceDateDesc(String symbol);

    @Query("SELECT sp FROM StockPrice sp WHERE sp.symbol = :symbol ORDER BY sp.priceDate DESC")
    List<StockPrice> findRecentBySymbol(String symbol);

    void deleteBySymbol(String symbol);
}
