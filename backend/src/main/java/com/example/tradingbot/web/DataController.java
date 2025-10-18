package com.example.tradingbot.web;

import com.example.tradingbot.domain.PortfolioValuePoint;
import com.example.tradingbot.market.Kline;
import com.example.tradingbot.market.MarketDataClient;
import com.example.tradingbot.repository.PortfolioValueRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
public class DataController {
    private final MarketDataClient marketDataClient;
    private final PortfolioValueRepository portfolioValueRepository;

    public DataController(MarketDataClient marketDataClient, PortfolioValueRepository portfolioValueRepository) {
        this.marketDataClient = marketDataClient;
        this.portfolioValueRepository = portfolioValueRepository;
    }

    @GetMapping("/klines")
    public ResponseEntity<?> klines(@RequestParam String symbol,
                                    @RequestParam(defaultValue = "1m") String interval,
                                    @RequestParam(defaultValue = "200") int limit) {
        List<Kline> data = marketDataClient.fetchKlines(symbol, interval, null, null, limit);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/portfolio")
    public ResponseEntity<?> portfolio(@RequestParam(defaultValue = "200") int limit) {
        List<PortfolioValuePoint> points = portfolioValueRepository.findRecent(limit);
        return ResponseEntity.ok(points);
    }
}
