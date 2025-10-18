package com.example.tradingbot.domain;

import java.math.BigDecimal;
import java.time.Instant;

public class PortfolioValuePoint {
    private long id;
    private Instant timestamp;
    private BigDecimal totalValue;
    private BigDecimal cash;
    private BigDecimal holdingsValue;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public BigDecimal getTotalValue() { return totalValue; }
    public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
    public BigDecimal getCash() { return cash; }
    public void setCash(BigDecimal cash) { this.cash = cash; }
    public BigDecimal getHoldingsValue() { return holdingsValue; }
    public void setHoldingsValue(BigDecimal holdingsValue) { this.holdingsValue = holdingsValue; }
}
