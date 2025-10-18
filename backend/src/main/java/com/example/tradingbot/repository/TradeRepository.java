package com.example.tradingbot.repository;

import com.example.tradingbot.domain.Trade;
import com.example.tradingbot.domain.TradeSide;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Repository
public class TradeRepository {
    private final JdbcTemplate jdbc;

    private static final RowMapper<Trade> ROW_MAPPER = new RowMapper<Trade>() {
        @Override
        public Trade mapRow(ResultSet rs, int rowNum) throws SQLException {
            Trade t = new Trade();
            t.setId(rs.getLong("id"));
            t.setTimestamp(rs.getTimestamp("ts").toInstant());
            t.setSymbol(rs.getString("symbol"));
            t.setSide(TradeSide.valueOf(rs.getString("side")));
            t.setQuantity(rs.getBigDecimal("quantity"));
            t.setPrice(rs.getBigDecimal("price"));
            t.setFee(rs.getBigDecimal("fee"));
            t.setRealizedPnl(rs.getBigDecimal("realized_pnl"));
            return t;
        }
    };

    public TradeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Trade> findRecent(int limit) {
        return jdbc.query("SELECT * FROM trades ORDER BY ts DESC LIMIT ?", ROW_MAPPER, limit);
    }

    public void insert(Instant ts, String symbol, TradeSide side, BigDecimal qty, BigDecimal price, BigDecimal fee, BigDecimal realizedPnl) {
        jdbc.update("INSERT INTO trades(ts, symbol, side, quantity, price, fee, realized_pnl) VALUES (?,?,?,?,?,?,?)",
                java.sql.Timestamp.from(ts), symbol, side.name(), qty, price, fee, realizedPnl);
    }
}
