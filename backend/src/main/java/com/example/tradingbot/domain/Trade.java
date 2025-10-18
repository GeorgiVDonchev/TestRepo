package com.example.tradingbot.domain;

import java.math.BigDecimal;
import java.time.Instant;

public class Trade {
    private long id;
    private Instant timestamp;
    private String symbol;
    private TradeSide side;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal fee;
    private BigDecimal realizedPnl;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public TradeSide getSide() { return side; }
    public void setSide(TradeSide side) { this.side = side; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
    public BigDecimal getRealizedPnl() { return realizedPnl; }
    public void setRealizedPnl(BigDecimal realizedPnl) { this.realizedPnl = realizedPnl; }
}
