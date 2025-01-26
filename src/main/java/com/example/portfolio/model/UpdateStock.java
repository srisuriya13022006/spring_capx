package com.example.portfolio.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "updatestocks")
public class UpdateStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ticker_symbol", nullable = false, unique = true)
    private String tickerSymbol;

    @Column(nullable = false)
    private double currentPrice;

    @Transient
    private double profitOrLoss; // Calculate profit/loss dynamically

    @Column(name = "last_updated", nullable = false)
    private Timestamp lastUpdated; // Timestamp for last update

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTickerSymbol() {
        return tickerSymbol;
    }

    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // Calculate profit/loss dynamically based on buy price from Stock model
    public double getProfitOrLoss(double buyPrice) {
        return currentPrice - buyPrice; // Positive for profit, negative for loss
    }

    // Optional: Calculate profit or loss percentage
    public double getProfitOrLossPercentage(double buyPrice) {
        if (buyPrice == 0) {
            return 0;
        }
        return ((currentPrice - buyPrice) / buyPrice) * 100;
    }
}
