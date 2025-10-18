package com.example.tradingbot.repository;

import com.example.tradingbot.domain.PortfolioValuePoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Repository
public class PortfolioValueRepository {
    private final JdbcTemplate jdbc;

    private static final RowMapper<PortfolioValuePoint> ROW_MAPPER = new RowMapper<PortfolioValuePoint>() {
        @Override
        public PortfolioValuePoint mapRow(ResultSet rs, int rowNum) throws SQLException {
            PortfolioValuePoint p = new PortfolioValuePoint();
            p.setId(rs.getLong("id"));
            p.setTimestamp(rs.getTimestamp("ts").toInstant());
            p.setTotalValue(rs.getBigDecimal("total_value"));
            p.setCash(rs.getBigDecimal("cash"));
            p.setHoldingsValue(rs.getBigDecimal("holdings_value"));
            return p;
        }
    };

    public PortfolioValueRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<PortfolioValuePoint> findRecent(int limit) {
        return jdbc.query("SELECT * FROM portfolio_value_history ORDER BY ts DESC LIMIT ?", ROW_MAPPER, limit);
    }

    public void insert(Instant ts, java.math.BigDecimal total, java.math.BigDecimal cash, java.math.BigDecimal holdingsValue) {
        jdbc.update("INSERT INTO portfolio_value_history(ts, total_value, cash, holdings_value) VALUES (?,?,?,?)",
                java.sql.Timestamp.from(ts), total, cash, holdingsValue);
    }
}
