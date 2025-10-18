package com.cryptobot.repository;

import com.cryptobot.model.Trade;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class TradeRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public TradeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private final RowMapper<Trade> tradeRowMapper = new RowMapper<Trade>() {
        @Override
        public Trade mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Trade.builder()
                    .id(rs.getLong("id"))
                    .symbol(rs.getString("symbol"))
                    .action(rs.getString("action"))
                    .quantity(rs.getBigDecimal("quantity"))
                    .price(rs.getBigDecimal("price"))
                    .totalValue(rs.getBigDecimal("total_value"))
                    .profitLoss(rs.getBigDecimal("profit_loss"))
                    .balanceAfter(rs.getBigDecimal("balance_after"))
                    .timestamp(rs.getTimestamp("timestamp").toLocalDateTime())
                    .mode(rs.getString("mode"))
                    .strategyReason(rs.getString("strategy_reason"))
                    .build();
        }
    };
    
    public List<Trade> getAllTrades() {
        String sql = "SELECT * FROM trade_history ORDER BY timestamp DESC";
        return jdbcTemplate.query(sql, tradeRowMapper);
    }
    
    public List<Trade> getTradesByMode(String mode) {
        String sql = "SELECT * FROM trade_history WHERE mode = ? ORDER BY timestamp DESC";
        return jdbcTemplate.query(sql, tradeRowMapper, mode);
    }
    
    public void saveTrade(Trade trade) {
        String sql = "INSERT INTO trade_history (symbol, action, quantity, price, total_value, " +
                    "profit_loss, balance_after, timestamp, mode, strategy_reason) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                trade.getSymbol(),
                trade.getAction(),
                trade.getQuantity(),
                trade.getPrice(),
                trade.getTotalValue(),
                trade.getProfitLoss(),
                trade.getBalanceAfter(),
                Timestamp.valueOf(trade.getTimestamp()),
                trade.getMode(),
                trade.getStrategyReason()
        );
    }
    
    public void clearTrades() {
        String sql = "DELETE FROM trade_history";
        jdbcTemplate.update(sql);
    }
}
