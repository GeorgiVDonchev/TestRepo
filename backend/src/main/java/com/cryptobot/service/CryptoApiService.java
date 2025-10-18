package com.cryptobot.service;

import com.cryptobot.model.PriceData;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CryptoApiService {
    
    private final WebClient webClient;
    
    @Value("${bot.api.coingecko.url}")
    private String apiBaseUrl;
    
    public CryptoApiService() {
        this.webClient = WebClient.builder().build();
    }
    
    /**
     * Fetch current price for a cryptocurrency
     */
    public PriceData getCurrentPrice(String symbol) {
        try {
            String url = apiBaseUrl + "/simple/price?ids=" + symbol + 
                        "&vs_currencies=usd&include_market_cap=true&include_24hr_vol=true";
            
            JsonNode response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
            
            if (response != null && response.has(symbol)) {
                JsonNode data = response.get(symbol);
                return PriceData.builder()
                        .symbol(symbol)
                        .price(new BigDecimal(data.get("usd").asText()))
                        .volume(data.has("usd_24h_vol") ? 
                               new BigDecimal(data.get("usd_24h_vol").asText()) : BigDecimal.ZERO)
                        .marketCap(data.has("usd_market_cap") ? 
                                  new BigDecimal(data.get("usd_market_cap").asText()) : BigDecimal.ZERO)
                        .timestamp(LocalDateTime.now())
                        .build();
            }
        } catch (Exception e) {
            System.err.println("Error fetching price: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Fetch historical price data for training mode
     * Using market_chart endpoint for last 30 days
     */
    public List<PriceData> getHistoricalPrices(String symbol, int days) {
        List<PriceData> priceDataList = new ArrayList<>();
        
        try {
            String url = apiBaseUrl + "/coins/" + symbol + 
                        "/market_chart?vs_currency=usd&days=" + days + "&interval=daily";
            
            JsonNode response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();
            
            if (response != null && response.has("prices")) {
                JsonNode prices = response.get("prices");
                for (JsonNode price : prices) {
                    long timestamp = price.get(0).asLong();
                    BigDecimal priceValue = new BigDecimal(price.get(1).asText());
                    
                    priceDataList.add(PriceData.builder()
                            .symbol(symbol)
                            .price(priceValue)
                            .timestamp(LocalDateTime.now().minusDays(days).plusDays(priceDataList.size()))
                            .build());
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching historical prices: " + e.getMessage());
        }
        
        return priceDataList;
    }
}
