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
public class PriceData {
    private Long id;
    private String symbol;
    private BigDecimal price;
    private BigDecimal volume;
    private BigDecimal marketCap;
    private LocalDateTime timestamp;
}
