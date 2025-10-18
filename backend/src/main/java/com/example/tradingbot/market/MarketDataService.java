package com.example.tradingbot.market;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class MarketDataService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.market.base-url}")
    private String baseUrl;

    public List<Candle> fetchHistoricalKlines(String symbol, String interval, int limit) {
        String url = String.format("%s/api/v3/klines?symbol=%s&interval=%s&limit=%d", baseUrl, symbol, interval, limit);
        try {
            ResponseEntity<Object[][]> response = restTemplate.getForEntity(url, Object[][].class);
            Object[][] data = response.getBody();
            List<Candle> candles = new ArrayList<>();
            if (data != null) {
                for (Object[] row : data) {
                    long openTime = ((Number) row[0]).longValue();
                    double open = Double.parseDouble(row[1].toString());
                    double high = Double.parseDouble(row[2].toString());
                    double low = Double.parseDouble(row[3].toString());
                    double close = Double.parseDouble(row[4].toString());
                    long closeTime = ((Number) row[6]).longValue();
                    double volume = Double.parseDouble(row[5].toString());
                    candles.add(new Candle(openTime, open, high, low, close, closeTime, volume));
                }
            }
            return candles;
        } catch (RestClientException e) {
            return List.of();
        }
    }

    public Double fetchLivePrice(String symbol) {
        String url = String.format("%s/api/v3/ticker/price?symbol=%s", baseUrl, symbol);
        try {
            @SuppressWarnings("unchecked")
            var map = restTemplate.getForObject(url, java.util.Map.class);
            if (map != null && map.get("price") != null) {
                return Double.parseDouble(map.get("price").toString());
            }
            return null;
        } catch (RestClientException e) {
            return null;
        }
    }

    public long nowMs() {
        return Instant.now().toEpochMilli();
    }
}
