		package com.example.portfolio.service;
		
		import org.springframework.beans.factory.annotation.Value;
		import org.springframework.stereotype.Service;
		import org.springframework.web.client.RestTemplate;
		import org.springframework.web.util.UriComponentsBuilder;
		import org.json.JSONObject;
		
		@Service
		public class AlphaVantageService {
		
		    @Value("${alphavantage.api.key}")
		    private String apiKey;
		
		    private static final String BASE_URL = "https://www.alphavantage.co/query";
		
		    public Double getCurrentPrice(String symbol) {
		        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
		            .queryParam("function", "TIME_SERIES_INTRADAY")
		            .queryParam("symbol", symbol)
		            .queryParam("interval", "5min")
		            .queryParam("apikey", apiKey)
		            .toUriString();

		        RestTemplate restTemplate = new RestTemplate();
		        String response = restTemplate.getForObject(url, String.class);

		        if (response != null) {
		            System.out.println("API Response: " + response); // Log the response
		            JSONObject jsonResponse = new JSONObject(response);
		            JSONObject timeSeries = jsonResponse.optJSONObject("Time Series (5min)");

		            if (timeSeries != null) {
		                String latestTimestamp = timeSeries.keys().next();
		                JSONObject latestData = timeSeries.getJSONObject(latestTimestamp);
		                String priceString = latestData.getString("4. close"); // Close price
		                return Double.parseDouble(priceString);
		            }
		        }
		        return null; // Return null if the price couldn't be fetched
		    }

		}
