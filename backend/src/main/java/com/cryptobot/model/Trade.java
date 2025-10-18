package com.cryptobot.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trade {
    private Long id;
    private String symbol;
    private String action; // BUY or SELL
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal totalValue;
    private BigDecimal profitLoss;
    private BigDecimal balanceAfter;
    private LocalDateTime timestamp;
    private String mode; // TRAINING or TRADING
    private String strategyReason;
}
