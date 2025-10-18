package com.cryptobot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private Long id;
    private BigDecimal balance;
    private BigDecimal initialBalance;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
}
