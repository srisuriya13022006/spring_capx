package com.example.portfolio.repository;

import com.example.portfolio.model.UpdateStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UpdateStockRepository extends JpaRepository<UpdateStock, Long> {
    UpdateStock findByTickerSymbol(String tickerSymbol);
}
