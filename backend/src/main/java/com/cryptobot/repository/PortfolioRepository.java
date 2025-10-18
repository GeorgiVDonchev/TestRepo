package com.cryptobot.repository;

import com.cryptobot.model.Portfolio;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class PortfolioRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public PortfolioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private final RowMapper<Portfolio> portfolioRowMapper = new RowMapper<Portfolio>() {
        @Override
        public Portfolio mapRow(ResultSet rs, int rowNum) throws SQLException {
            Portfolio portfolio = new Portfolio();
            portfolio.setId(rs.getLong("id"));
            portfolio.setSymbol(rs.getString("symbol"));
            portfolio.setQuantity(rs.getBigDecimal("quantity"));
            portfolio.setAverageBuyPrice(rs.getBigDecimal("average_buy_price"));
            portfolio.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return portfolio;
        }
    };
    
    public List<Portfolio> getAllHoldings() {
        String sql = "SELECT * FROM portfolio WHERE quantity > 0";
        return jdbcTemplate.query(sql, portfolioRowMapper);
    }
    
    public Portfolio getHolding(String symbol) {
        String sql = "SELECT * FROM portfolio WHERE symbol = ?";
        List<Portfolio> results = jdbcTemplate.query(sql, portfolioRowMapper, symbol);
        return results.isEmpty() ? null : results.get(0);
    }
    
    public void upsertHolding(String symbol, BigDecimal quantity, BigDecimal averageBuyPrice) {
        String sql = "INSERT INTO portfolio (symbol, quantity, average_buy_price) " +
                    "VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE quantity = ?, average_buy_price = ?";
        jdbcTemplate.update(sql, symbol, quantity, averageBuyPrice, quantity, averageBuyPrice);
    }
    
    public void clearPortfolio() {
        String sql = "DELETE FROM portfolio";
        jdbcTemplate.update(sql);
    }
}
