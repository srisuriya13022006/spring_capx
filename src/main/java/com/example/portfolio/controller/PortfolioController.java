package com.example.portfolio.controller;

import com.example.portfolio.model.Stock;
import com.example.portfolio.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    @Autowired
    private StockService stockService;

    // Get portfolio metrics
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getPortfolioMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        // Get all stocks from the service
        List<Stock> stocks = stockService.getAllStocks();

        // Calculate total portfolio value
        double totalValue = stocks.stream().mapToDouble(stock -> stock.getQuantity() * stock.getPrice()).sum();
        metrics.put("totalValue", totalValue);

        // Find the top-performing stock (you can define your own logic here)
        Stock topStock = stocks.stream()
                .max(Comparator.comparingDouble(stock -> stock.getQuantity() * stock.getPrice()))
                .orElse(null);
        metrics.put("topStock", topStock != null ? topStock.getCompanyName() : "N/A");

        // You can add portfolio distribution logic here
        // Assuming you want to calculate the distribution of stock values (just a basic example)
        Map<String, Double> distribution = new HashMap<>();
        for (Stock stock : stocks) {
            double stockValue = stock.getQuantity() * stock.getPrice();
            distribution.put(stock.getCompanyName(), stockValue);
        }
        metrics.put("portfolioDistribution", distribution);

        return ResponseEntity.ok(metrics);
    }
}
