package com.example.tradingbot.api;

import com.example.tradingbot.db.SqlRepository;
import com.example.tradingbot.market.MarketDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data")
public class DataController {
    private final MarketDataService market;
    private final SqlRepository repo;

    public DataController(MarketDataService market, SqlRepository repo) {
        this.market = market;
        this.repo = repo;
    }

    @GetMapping("/klines")
    public List<?> klines(@RequestParam(defaultValue = "BTCUSDT") String symbol,
                          @RequestParam(defaultValue = "1m") String interval,
                          @RequestParam(defaultValue = "200") int limit) {
        return market.fetchHistoricalKlines(symbol, interval, limit);
    }

    @GetMapping("/trades")
    public List<Map<String, Object>> trades(@RequestParam(defaultValue = "100") int limit) {
        return repo.listTrades(limit);
    }

    @GetMapping("/snapshots")
    public List<Map<String, Object>> snapshots(@RequestParam(defaultValue = "200") int limit) {
        return repo.listSnapshots(limit);
    }
}
