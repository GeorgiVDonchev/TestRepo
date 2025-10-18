package com.example.tradingbot.web;

import com.example.tradingbot.config.AppProperties;
import com.example.tradingbot.repository.AccountRepository;
import com.example.tradingbot.repository.HoldingRepository;
import com.example.tradingbot.repository.TradeRepository;
import com.example.tradingbot.service.PortfolioService;
import com.example.tradingbot.service.TradingEngine;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/bot")
public class BotController {
    private final TradingEngine tradingEngine;
    private final PortfolioService portfolioService;
    private final TradeRepository tradeRepository;
    private final AccountRepository accountRepository;
    private final HoldingRepository holdingRepository;
    private final AppProperties appProperties;

    public BotController(TradingEngine tradingEngine,
                         PortfolioService portfolioService,
                         TradeRepository tradeRepository,
                         AccountRepository accountRepository,
                         HoldingRepository holdingRepository,
                         AppProperties appProperties) {
        this.tradingEngine = tradingEngine;
        this.portfolioService = portfolioService;
        this.tradeRepository = tradeRepository;
        this.accountRepository = accountRepository;
        this.holdingRepository = holdingRepository;
        this.appProperties = appProperties;
    }

    @PostMapping("/train")
    public ResponseEntity<?> train(@RequestBody Map<String, Object> body) {
        String symbol = (String) body.getOrDefault("symbol", appProperties.getDefaultSymbol());
        String interval = (String) body.getOrDefault("interval", appProperties.getDefaultInterval());
        int shortSma = ((Number) body.getOrDefault("shortSma", 20)).intValue();
        int longSma = ((Number) body.getOrDefault("longSma", 50)).intValue();
        int lookback = ((Number) body.getOrDefault("lookback", 500)).intValue();
        var res = tradingEngine.runTraining(symbol, interval, shortSma, longSma, lookback);
        return ResponseEntity.ok(Map.of("trades", res.trades(), "returnPct", res.returnPct()));
    }

    @PostMapping("/live/step")
    public ResponseEntity<?> liveStep(@RequestBody Map<String, Object> body) {
        String symbol = (String) body.getOrDefault("symbol", appProperties.getDefaultSymbol());
        tradingEngine.runLiveStep(symbol);
        portfolioService.snapshotPortfolioValue(symbol);
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @PostMapping("/reset")
    public ResponseEntity<?> reset() {
        accountRepository.setCashBalance(java.math.BigDecimal.valueOf(appProperties.getInitialCash()));
        holdingRepository.upsert(appProperties.getDefaultSymbol(), java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO);
        return ResponseEntity.ok(Map.of("status", "reset"));
    }

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        var account = accountRepository.getDefault().orElseThrow();
        var trades = tradeRepository.findRecent(200);
        var holding = holdingRepository.findBySymbol(appProperties.getDefaultSymbol());
        return ResponseEntity.ok(Map.of(
                "account", account,
                "trades", trades,
                "holding", holding.orElse(null)
        ));
    }
}
