package com.example.tradingbot.service;

import com.example.tradingbot.domain.Holding;
import com.example.tradingbot.market.MarketDataClient;
import com.example.tradingbot.repository.AccountRepository;
import com.example.tradingbot.repository.HoldingRepository;
import com.example.tradingbot.repository.PortfolioValueRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class PortfolioService {
    private final AccountRepository accountRepository;
    private final HoldingRepository holdingRepository;
    private final PortfolioValueRepository portfolioValueRepository;
    private final MarketDataClient marketDataClient;

    public PortfolioService(AccountRepository accountRepository,
                            HoldingRepository holdingRepository,
                            PortfolioValueRepository portfolioValueRepository,
                            MarketDataClient marketDataClient) {
        this.accountRepository = accountRepository;
        this.holdingRepository = holdingRepository;
        this.portfolioValueRepository = portfolioValueRepository;
        this.marketDataClient = marketDataClient;
    }

    public void snapshotPortfolioValue(String symbol) {
        var account = accountRepository.getDefault().orElseThrow();
        List<Holding> holdings = holdingRepository.findAll();
        double price = marketDataClient.fetchCurrentPrice(symbol);
        BigDecimal holdingsValue = BigDecimal.ZERO;
        for (Holding h : holdings) {
            if (h.getQuantity().doubleValue() != 0.0) {
                holdingsValue = holdingsValue.add(h.getQuantity().multiply(BigDecimal.valueOf(price)));
            }
        }
        BigDecimal total = holdingsValue.add(account.getCashBalance());
        portfolioValueRepository.insert(Instant.now(), total, account.getCashBalance(), holdingsValue);
    }
}
