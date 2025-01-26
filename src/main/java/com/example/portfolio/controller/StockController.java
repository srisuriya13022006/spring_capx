package com.example.portfolio.controller;

import com.example.portfolio.model.Stock;
import com.example.portfolio.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/stocks")
public class StockController {

    @Autowired
    private StockService stockService;

    // Create or update stock
    @PostMapping
    public ResponseEntity<Stock> saveStock(@RequestBody Stock stock) {
        Stock savedStock = stockService.saveStock(stock);
        return ResponseEntity.ok(savedStock);
    }

    // Get all stocks
    @GetMapping
    public ResponseEntity<List<Stock>> getAllStocks() {
        List<Stock> stocks = stockService.getAllStocks();
        return ResponseEntity.ok(stocks);
    }

    // Get stock by ticker symbol
    @GetMapping("/ticker/{tickerSymbol}")
    public ResponseEntity<Stock> getStockByTickerSymbol(@PathVariable String tickerSymbol) {
        Stock stock = stockService.getStockByTickerSymbol(tickerSymbol);
        if (stock != null) {
            return ResponseEntity.ok(stock);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/portfolio/metrics")
    public ResponseEntity<Map<String, Object>> getPortfolioMetrics() {
        Map<String, Object> metrics = stockService.getPortfolioMetrics();
        return ResponseEntity.ok(metrics);
    }

    // Update stock price by ticker symbol and update updatestocks
    @PutMapping("/updatePrice/{tickerSymbol}")
    public ResponseEntity<Stock> updateStockPrice(@PathVariable String tickerSymbol) {
        Stock updatedStock = stockService.updateStockPrice(tickerSymbol);
        if (updatedStock != null) {
            return ResponseEntity.ok(updatedStock);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
 // Fetch real-time price for a specific stock ticker symbol
    @GetMapping("/realtime/{tickerSymbol}")
    public ResponseEntity<?> getRealTimePrice(@PathVariable String tickerSymbol) {
        Double currentPrice = stockService.getRealTimePrice(tickerSymbol); // Assuming your StockService has a method to fetch real-time prices
        if (currentPrice != null) {
            return ResponseEntity.ok().body(Map.of("currentPrice", currentPrice));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Real-time price not found for ticker: " + tickerSymbol);
        }
    }


    // Delete stock by ticker symbol
    @DeleteMapping("/{ticker}")
    public ResponseEntity<Void> deleteStockByTicker(@PathVariable String ticker) {
        stockService.deleteStockByTicker(ticker);
        return ResponseEntity.noContent().build();
    }

    // Update all stocks with real-time prices and save to updatestocks
    @PutMapping("/updateAllPrices")
    public ResponseEntity<String> updateAllPrices() {
        stockService.updateAllStocksWithCurrentPrices();
        return ResponseEntity.ok("Stock prices updated successfully");
    }

    // Update stock by ticker symbol (manual update of stock fields)
    @PutMapping("/ticker/{ticker}")
    public ResponseEntity<Stock> updateStockByTicker(@PathVariable String ticker, @RequestBody Stock updatedStock) {
        Stock stock = stockService.getStockByTickerSymbol(ticker);
        if (stock != null) {
            stock.setCompanyName(updatedStock.getCompanyName());
            stock.setPrice(updatedStock.getPrice());
            stock.setQuantity(updatedStock.getQuantity()); // Update the quantity as well
            Stock savedStock = stockService.saveStock(stock);
            return ResponseEntity.ok(savedStock);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
