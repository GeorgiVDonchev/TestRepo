package com.example.tradingbot.bot;

import com.example.tradingbot.db.SqlRepository;
import com.example.tradingbot.market.Candle;
import com.example.tradingbot.market.MarketDataService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

@Service
public class TradingBotService {
    public enum Mode { BACKTEST, LIVE_SIM, PAUSED }

    private final MarketDataService marketDataService;
    private final SqlRepository repo;

    private volatile Mode mode = Mode.PAUSED;
    private volatile String symbol = "BTCUSDT";

    @Value("${app.bot.poll-interval-seconds:5}")
    private int pollIntervalSeconds;

    // State for SMA strategy
    private final int shortWindow = 7;
    private final int longWindow = 25;
    private final Deque<Double> shortWindowPrices = new ArrayDeque<>();
    private final Deque<Double> longWindowPrices = new ArrayDeque<>();

    public TradingBotService(MarketDataService marketDataService, SqlRepository repo) {
        this.marketDataService = marketDataService;
        this.repo = repo;
    }

    public synchronized void startBacktest(String symbol) {
        this.symbol = symbol;
        resetState();
        this.mode = Mode.BACKTEST;
    }

    public synchronized void startLiveSim(String symbol) {
        this.symbol = symbol;
        resetState();
        this.mode = Mode.LIVE_SIM;
    }

    public synchronized void pause() {
        this.mode = Mode.PAUSED;
    }

    public synchronized void reset() {
        pause();
        double currentCash = ((Number) repo.getAccount().get("cash_balance")).doubleValue();
        repo.updateCashBalance(10000 - currentCash);
        // Reset commonly used holdings to zero
        repo.setHolding("BTC", 0);
        repo.setHolding("ETH", 0);
        repo.clearTradesAndSnapshots();
        resetState();
    }

    private void resetState() {
        shortWindowPrices.clear();
        longWindowPrices.clear();
    }

    @Scheduled(fixedDelayString = "${app.bot.poll-interval-seconds:5}000")
    public void tick() {
        if (mode == Mode.PAUSED) return;

        if (mode == Mode.BACKTEST) {
            runBacktestTick();
        } else if (mode == Mode.LIVE_SIM) {
            runLiveSimTick();
        }
    }

    private void runBacktestTick() {
        List<Candle> candles = marketDataService.fetchHistoricalKlines(symbol, "1m", 200);
        if (candles.isEmpty()) return;
        for (Candle c : candles) {
            processPrice(c.getClose());
            snapshotPortfolio(c.getClose());
        }
        // Pause after one pass of backtest per tick to avoid infinite work
        pause();
    }

    private void runLiveSimTick() {
        Double price = marketDataService.fetchLivePrice(symbol);
        if (price == null) return;
        processPrice(price);
        snapshotPortfolio(price);
    }

    private void processPrice(double price) {
        updateWindows(price);
        if (longWindowPrices.size() < longWindow) return;
        double smaShort = average(shortWindowPrices);
        double smaLong = average(longWindowPrices);
        double holdingQty = repo.getHolding(baseAsset());
        double cash = ((Number) repo.getAccount().get("cash_balance")).doubleValue();

        // Simple SMA crossover
        if (smaShort > smaLong * 1.001 && cash > 10) {
            // Buy for 20% of cash
            double spend = cash * 0.2;
            double qty = spend / price;
            repo.updateCashBalance(-spend);
            repo.setHolding(baseAsset(), holdingQty + qty);
            repo.insertTrade(symbol, "BUY", qty, price, 0);
        } else if (smaShort < smaLong * 0.999 && holdingQty > 0.00001) {
            // Sell 20% of holdings
            double qty = holdingQty * 0.2;
            double proceeds = qty * price;
            repo.updateCashBalance(proceeds);
            repo.setHolding(baseAsset(), holdingQty - qty);
            // Realized PnL: approximated, not tracking per-lot cost here
            repo.insertTrade(symbol, "SELL", qty, price, 0);
        }
    }

    private void snapshotPortfolio(double lastPrice) {
        double cash = ((Number) repo.getAccount().get("cash_balance")).doubleValue();
        double qty = repo.getHolding(baseAsset());
        double totalValue = cash + qty * lastPrice;
        repo.insertSnapshot(totalValue);
    }

    private void updateWindows(double price) {
        shortWindowPrices.addLast(price);
        if (shortWindowPrices.size() > shortWindow) shortWindowPrices.removeFirst();
        longWindowPrices.addLast(price);
        if (longWindowPrices.size() > longWindow) longWindowPrices.removeFirst();
    }

    private double average(Deque<Double> deque) {
        double sum = 0;
        for (double d : deque) sum += d;
        return sum / deque.size();
    }

    private String baseAsset() {
        if (symbol != null && symbol.endsWith("USDT")) return symbol.substring(0, symbol.length() - 4);
        if (symbol != null && symbol.endsWith("USD")) return symbol.substring(0, symbol.length() - 3);
        return symbol;
    }

    public Mode getMode() { return mode; }
    public String getSymbol() { return symbol; }
}
