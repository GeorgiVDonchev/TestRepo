package com.example.tradingbot.market;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class MarketDataClient {
    private final WebClient webClient;

    public MarketDataClient(WebClient webClient) {
        this.webClient = webClient.mutate()
                .baseUrl("https://api.binance.com")
                .build();
    }

    public List<Kline> fetchKlines(String symbol, String interval, Long startTime, Long endTime, int limit) {
        WebClient.RequestHeadersUriSpec<?> req = webClient.get();
        WebClient.RequestHeadersSpec<?> spec = req.uri(uriBuilder -> {
            var builder = uriBuilder.path("/api/v3/klines")
                    .queryParam("symbol", symbol)
                    .queryParam("interval", interval)
                    .queryParam("limit", limit);
            if (startTime != null) builder.queryParam("startTime", startTime);
            if (endTime != null) builder.queryParam("endTime", endTime);
            return builder.build();
        });
        Mono<List> mono = spec.accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(List.class);
        List raw = mono.block();
        List<Kline> result = new ArrayList<>();
        if (raw == null) return result;
        for (Object row : raw) {
            List<?> arr = (List<?>) row;
            Kline k = new Kline();
            k.setOpenTime(Instant.ofEpochMilli(((Number) arr.get(0)).longValue()));
            k.setOpen(Double.parseDouble(arr.get(1).toString()));
            k.setHigh(Double.parseDouble(arr.get(2).toString()));
            k.setLow(Double.parseDouble(arr.get(3).toString()));
            k.setClose(Double.parseDouble(arr.get(4).toString()));
            k.setVolume(Double.parseDouble(arr.get(5).toString()));
            k.setCloseTime(Instant.ofEpochMilli(((Number) arr.get(6)).longValue()));
            result.add(k);
        }
        return result;
    }

    public double fetchCurrentPrice(String symbol) {
        var map = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v3/ticker/price")
                        .queryParam("symbol", symbol)
                        .build())
                .retrieve()
                .bodyToMono(java.util.Map.class)
                .block();
        if (map == null) return Double.NaN;
        return Double.parseDouble(map.get("price").toString());
    }
}
