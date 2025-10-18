package com.cryptobot.repository;

import com.cryptobot.model.PriceData;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class PriceHistoryRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public PriceHistoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private final RowMapper<PriceData> priceDataRowMapper = new RowMapper<PriceData>() {
        @Override
        public PriceData mapRow(ResultSet rs, int rowNum) throws SQLException {
            return PriceData.builder()
                    .id(rs.getLong("id"))
                    .symbol(rs.getString("symbol"))
                    .price(rs.getBigDecimal("price"))
                    .volume(rs.getBigDecimal("volume"))
                    .marketCap(rs.getBigDecimal("market_cap"))
                    .timestamp(rs.getTimestamp("timestamp").toLocalDateTime())
                    .build();
        }
    };
    
    public List<PriceData> getRecentPrices(String symbol, int limit) {
        String sql = "SELECT * FROM price_history WHERE symbol = ? " +
                    "ORDER BY timestamp DESC LIMIT ?";
        return jdbcTemplate.query(sql, priceDataRowMapper, symbol, limit);
    }
    
    public void savePriceData(PriceData priceData) {
        String sql = "INSERT INTO price_history (symbol, price, volume, market_cap, timestamp) " +
                    "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                priceData.getSymbol(),
                priceData.getPrice(),
                priceData.getVolume(),
                priceData.getMarketCap(),
                Timestamp.valueOf(priceData.getTimestamp())
        );
    }
}
