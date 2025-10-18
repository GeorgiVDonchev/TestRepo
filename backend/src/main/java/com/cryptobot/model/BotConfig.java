package com.cryptobot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BotConfig {
    private Long id;
    private String mode; // TRAINING or TRADING
    private String status; // RUNNING, PAUSED, STOPPED
    private String symbol;
    private Integer intervalSeconds;
    private String strategy;
    private LocalDateTime updatedAt;
}
