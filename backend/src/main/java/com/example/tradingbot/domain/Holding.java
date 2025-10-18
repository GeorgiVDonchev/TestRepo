package com.example.tradingbot.domain;

import java.math.BigDecimal;
import java.time.Instant;

public class Holding {
    private long id;
    private String symbol;
    private BigDecimal quantity;
    private BigDecimal avgCost;
    private Instant updatedAt;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getAvgCost() { return avgCost; }
    public void setAvgCost(BigDecimal avgCost) { this.avgCost = avgCost; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
