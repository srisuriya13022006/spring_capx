package com.example.portfolio.repository;

import com.example.portfolio.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
    // Custom query to find stock by its ticker symbol
   public  Stock findByTickerSymbol(String tickerSymbol);
}