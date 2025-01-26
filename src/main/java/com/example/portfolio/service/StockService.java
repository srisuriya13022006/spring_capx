package com.example.portfolio.service;

import com.example.portfolio.model.Stock;
import com.example.portfolio.model.UpdateStock;
import com.example.portfolio.repository.UpdateStockRepository;
import com.example.portfolio.repository.StockRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private UpdateStockRepository updateStockRepository;

    @Autowired
    private AlphaVantageService alphaVantageService; // Service to get real-time data from Alpha Vantage

    // Save or update stock
    public Stock saveStock(Stock stock) {
        System.out.println("Saving stock: " + stock.getTickerSymbol());
        Stock savedStock = stockRepository.save(stock);
        System.out.println("Saved stock successfully with ID: " + savedStock.getId());
        return savedStock;
    }

    // Get all stocks
    public List<Stock> getAllStocks() {
        System.out.println("Fetching all stocks");
        return stockRepository.findAll();
    }

    // Get stock by ID
    public Optional<Stock> getStockById(Long id) {
        System.out.println("Fetching stock with ID: " + id);
        return stockRepository.findById(id);
    }

    // Get stock by ticker symbol
    public Stock getStockByTickerSymbol(String tickerSymbol) {
        System.out.println("Fetching stock with ticker symbol: " + tickerSymbol);
        return stockRepository.findByTickerSymbol(tickerSymbol);
    }

    // Delete stock by ID
    public void deleteStockById(Long id) {
        System.out.println("Deleting stock with ID: " + id);
        stockRepository.deleteById(id);
        System.out.println("Stock deleted successfully");
    }

    // Save or update real-time stock data in updatestocks table
    public UpdateStock saveUpdateStock(UpdateStock updateStock) {
        System.out.println("Saving update for stock: " + updateStock.getTickerSymbol());
        UpdateStock savedUpdateStock = updateStockRepository.save(updateStock);
        System.out.println("Saved updated stock in updatestocks with ID: " + savedUpdateStock.getId());
        return savedUpdateStock;
    }

    // Get the updated stock by ticker symbol from updatestocks
    public UpdateStock getUpdateStockByTickerSymbol(String tickerSymbol) {
        System.out.println("Fetching updated stock for ticker symbol: " + tickerSymbol);
        return updateStockRepository.findByTickerSymbol(tickerSymbol);
    }

    // ** Modified: Update stock price by ticker symbol and save to updatestocks **
    public Stock updateStockPrice(String tickerSymbol) {
        System.out.println("Updating stock price for ticker symbol: " + tickerSymbol);
        
        Stock stock = stockRepository.findByTickerSymbol(tickerSymbol);
        System.out.println("Ticker symbol provided: " + tickerSymbol);

        if (stock != null) {
            System.out.println("Stock found: " + stock.getTickerSymbol() + ", Buy price: " + stock.getPrice());
            
            Double currentPrice = getRealTimePrice(tickerSymbol);  // Use the cached version
            System.out.println("Fetched current price: " + currentPrice);

            if (currentPrice != null) {
                UpdateStock existingUpdateStock = updateStockRepository.findByTickerSymbol(tickerSymbol);
                
                if (existingUpdateStock != null) {
                    System.out.println("Updating existing stock in updatestocks table: " + existingUpdateStock.getTickerSymbol());
                    existingUpdateStock.setCurrentPrice(currentPrice);
                    existingUpdateStock.getProfitOrLoss(stock.getPrice()); // Calculate profit/loss based on buy price
                    existingUpdateStock.setLastUpdated(new Timestamp(System.currentTimeMillis())); // Set the current timestamp
                    updateStockRepository.save(existingUpdateStock);
                    System.out.println("Updated existing stock with new price: " + currentPrice);
                } else {
                    System.out.println("Creating new entry in updatestocks table for: " + tickerSymbol);
                    UpdateStock newUpdateStock = new UpdateStock();
                    newUpdateStock.setTickerSymbol(tickerSymbol);
                    newUpdateStock.setCurrentPrice(currentPrice);
                    newUpdateStock.getProfitOrLoss(stock.getPrice()); // Calculate profit/loss based on buy price
                    newUpdateStock.setLastUpdated(new Timestamp(System.currentTimeMillis())); // Set the current timestamp
                    updateStockRepository.save(newUpdateStock);
                    System.out.println("Saved new stock with current price: " + currentPrice);
                }
                return stock;
            } else {
                System.out.println("Price not available for: " + tickerSymbol);
                return null;
            }
        } else {
            System.out.println("Stock not found for tickerSymbol: " + tickerSymbol);
            return null;
        }
    }

    // Delete stock by ticker symbol
    public void deleteStockByTicker(String ticker) {
        System.out.println("Deleting stock with ticker symbol: " + ticker);
        Stock stock = stockRepository.findByTickerSymbol(ticker); // Find the stock by ticker
        if (stock != null) {
            stockRepository.delete(stock); // Delete the stock if found
            System.out.println("Stock deleted successfully: " + ticker);
        } else {
            throw new RuntimeException("Stock with ticker symbol " + ticker + " not found");
        }
    }

    // ** Modified: Update all stock prices and save to updatestocks **
    @CacheEvict(value = "stockPrices", allEntries = true)  // Invalidate cache before updating all stocks
    public void updateAllStocksWithCurrentPrices() {
        System.out.println("Updating all stock prices");
        List<Stock> allStocks = stockRepository.findAll();
        for (Stock stock : allStocks) {
            System.out.println("Updating price for stock: " + stock.getTickerSymbol());
            updateStockPrice(stock.getTickerSymbol());
        }
    }

    // ** Cached real-time stock price **
    @Cacheable(value = "stockPrices", key = "#tickerSymbol", unless = "#result == null")
    public Double getRealTimePrice(String tickerSymbol) {
        System.out.println("Fetching real-time price for: " + tickerSymbol);
        return fetchPriceFromAPI(tickerSymbol); // Fetch from API if not cached
    }

    private Double fetchPriceFromAPI(String tickerSymbol) {
        try {
            String apiKey = "KAE2P1IIXLUE3R9D"; // Replace with your actual API key
            String apiUrl = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + tickerSymbol + "&apikey=" + apiKey;

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(apiUrl, String.class);

            // Log the full API response to debug
            System.out.println("API Response for " + tickerSymbol + ": " + response);

            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.has("Global Quote")) {
                JSONObject globalQuote = jsonResponse.getJSONObject("Global Quote");
                if (globalQuote.has("05. price")) { // Check if "05. price" is present
                    String priceString = globalQuote.getString("05. price");
                    return Double.parseDouble(priceString); // Return the current price
                } else {
                    System.out.println("Price not available in Global Quote for ticker: " + tickerSymbol);
                    return null;
                }
            } else {
                System.out.println("No Global Quote found for ticker: " + tickerSymbol);
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Double> getPortfolioDistribution() {
        List<Stock> allStocks = stockRepository.findAll();
        double totalValue = allStocks.stream()
                                     .mapToDouble(stock -> stock.getPrice() * stock.getQuantity())
                                     .sum();

        Map<String, Double> distribution = new HashMap<>();
        for (Stock stock : allStocks) {
            double stockValue = stock.getPrice() * stock.getQuantity();
            double percentage = (stockValue / totalValue) * 100;
            distribution.put(stock.getTickerSymbol(), percentage); // Store percentage by ticker
        }
        return distribution;
    }

    // Method to get total portfolio value, top stock, and distribution
    public Map<String, Object> getPortfolioMetrics() {
        List<Stock> allStocks = stockRepository.findAll();

        // Calculate total portfolio value
        double totalValue = allStocks.stream()
                                     .mapToDouble(stock -> stock.getPrice() * stock.getQuantity())
                                     .sum();

        // Find top performing stock (largest value)
        Stock topStock = allStocks.stream()
                                  .max(Comparator.comparing(stock -> stock.getPrice() * stock.getQuantity()))
                                  .orElse(null);

        // Calculate portfolio distribution
        Map<String, Double> portfolioDistribution = getPortfolioDistribution();

        // Build the result
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalValue", totalValue);
        metrics.put("topStock", topStock != null ? topStock.getTickerSymbol() : "None");
        metrics.put("portfolioDistribution", portfolioDistribution);

        return metrics;
    }
}

