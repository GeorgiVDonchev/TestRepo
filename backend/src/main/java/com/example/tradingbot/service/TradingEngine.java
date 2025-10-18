package com.example.tradingbot.service;

import com.example.tradingbot.config.AppProperties;
import com.example.tradingbot.domain.Holding;
import com.example.tradingbot.domain.TradeSide;
import com.example.tradingbot.market.Kline;
import com.example.tradingbot.market.MarketDataClient;
import com.example.tradingbot.repository.AccountRepository;
import com.example.tradingbot.repository.HoldingRepository;
import com.example.tradingbot.repository.TradeRepository;
import com.example.tradingbot.util.Indicators;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class TradingEngine {
    private final MarketDataClient marketDataClient;
    private final AccountRepository accountRepository;
    private final HoldingRepository holdingRepository;
    private final TradeRepository tradeRepository;
    private final AppProperties appProperties;

    public TradingEngine(MarketDataClient marketDataClient,
                         AccountRepository accountRepository,
                         HoldingRepository holdingRepository,
                         TradeRepository tradeRepository,
                         AppProperties appProperties) {
        this.marketDataClient = marketDataClient;
        this.accountRepository = accountRepository;
        this.holdingRepository = holdingRepository;
        this.tradeRepository = tradeRepository;
        this.appProperties = appProperties;
    }

    public record BacktestResult(int trades, double returnPct) {}

    public BacktestResult runTraining(String symbol, String interval, int shortSma, int longSma, int lookback) {
        List<Kline> klines = marketDataClient.fetchKlines(symbol, interval, null, null, lookback);
        List<Double> closes = new ArrayList<>();
        for (Kline k : klines) closes.add(k.getClose());
        List<Double> smaShort = Indicators.simpleMovingAverage(closes, shortSma);
        List<Double> smaLong = Indicators.simpleMovingAverage(closes, longSma);

        double cash = appProperties.getInitialCash();
        double positionQty = 0.0;
        double lastBuyPrice = 0.0;
        int tradeCount = 0;

        for (int i = 0; i < klines.size(); i++) {
            double price = closes.get(i);
            double s = smaShort.get(i);
            double l = smaLong.get(i);
            if (Double.isNaN(s) || Double.isNaN(l)) continue;

            if (s > l && positionQty == 0.0) {
                double qty = cash / price;
                double fee = qty * price * appProperties.getFeeRateBps() / 10000.0;
                cash -= qty * price + fee;
                positionQty += qty;
                lastBuyPrice = price;
                tradeCount++;
            } else if (s < l && positionQty > 0.0) {
                double proceeds = positionQty * price;
                double fee = proceeds * appProperties.getFeeRateBps() / 10000.0;
                cash += proceeds - fee;
                tradeCount++;
                positionQty = 0.0;
            }
        }
        double finalValue = cash + positionQty * closes.get(closes.size()-1);
        double retPct = (finalValue - appProperties.getInitialCash()) / appProperties.getInitialCash() * 100.0;
        return new BacktestResult(tradeCount, retPct);
    }

    public void runLiveStep(String symbol) {
        double price = marketDataClient.fetchCurrentPrice(symbol);
        var holding = holdingRepository.findBySymbol(symbol).orElseGet(() -> {
            Holding h = new Holding();
            h.setSymbol(symbol);
            h.setQuantity(BigDecimal.ZERO);
            h.setAvgCost(BigDecimal.ZERO);
            holdingRepository.upsert(symbol, BigDecimal.ZERO, BigDecimal.ZERO);
            return h;
        });
        double qty = holding.getQuantity().doubleValue();
        var account = accountRepository.getDefault().orElseThrow();
        double cash = account.getCashBalance().doubleValue();

        List<Kline> klines = marketDataClient.fetchKlines(symbol, "1m", null, null, 200);
        List<Double> closes = new ArrayList<>();
        for (Kline k : klines) closes.add(k.getClose());
        List<Double> shortSma = Indicators.simpleMovingAverage(closes, 20);
        List<Double> longSma = Indicators.simpleMovingAverage(closes, 50);
        double s = shortSma.get(shortSma.size()-1);
        double l = longSma.get(longSma.size()-1);

        if (!Double.isNaN(s) && !Double.isNaN(l)) {
            if (s > l && qty == 0.0 && cash > 10) {
                double buyQty = cash / price;
                double fee = buyQty * price * appProperties.getFeeRateBps() / 10000.0;
                double cost = buyQty * price + fee;
                accountRepository.setCashBalance(BigDecimal.valueOf(cash - cost));
                holdingRepository.upsert(symbol, BigDecimal.valueOf(buyQty), BigDecimal.valueOf(price));
                tradeRepository.insert(Instant.now(), symbol, TradeSide.BUY, BigDecimal.valueOf(buyQty), BigDecimal.valueOf(price), BigDecimal.valueOf(fee), BigDecimal.ZERO);
            } else if (s < l && qty > 0.0) {
                double proceeds = qty * price;
                double fee = proceeds * appProperties.getFeeRateBps() / 10000.0;
                double realized = (price - holding.getAvgCost().doubleValue()) * qty;
                accountRepository.setCashBalance(BigDecimal.valueOf(cash + proceeds - fee));
                holdingRepository.upsert(symbol, BigDecimal.ZERO, BigDecimal.ZERO);
                tradeRepository.insert(Instant.now(), symbol, TradeSide.SELL, holding.getQuantity(), BigDecimal.valueOf(price), BigDecimal.valueOf(fee), BigDecimal.valueOf(realized));
            }
        }
    }
}
