package com.example.tradingbot.api;

import com.example.tradingbot.bot.TradingBotService;
import com.example.tradingbot.db.SqlRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bot")
public class BotController {
    private final TradingBotService bot;
    private final SqlRepository repo;

    public BotController(TradingBotService bot, SqlRepository repo) {
        this.bot = bot;
        this.repo = repo;
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        return Map.of(
                "mode", bot.getMode().name(),
                "symbol", bot.getSymbol(),
                "account", repo.getAccount()
        );
    }

    @PostMapping("/backtest")
    public ResponseEntity<?> backtest(@RequestParam(defaultValue = "BTCUSDT") String symbol) {
        bot.startBacktest(symbol);
        return ResponseEntity.ok(Map.of("message", "Backtest started", "symbol", symbol));
    }

    @PostMapping("/live")
    public ResponseEntity<?> live(@RequestParam(defaultValue = "BTCUSDT") String symbol) {
        bot.startLiveSim(symbol);
        return ResponseEntity.ok(Map.of("message", "Live sim started", "symbol", symbol));
    }

    @PostMapping("/pause")
    public ResponseEntity<?> pause() {
        bot.pause();
        return ResponseEntity.ok(Map.of("message", "Paused"));
    }

    @PostMapping("/reset")
    public ResponseEntity<?> reset() {
        bot.reset();
        return ResponseEntity.ok(Map.of("message", "Reset"));
    }
}
