package com.example.tradingbot.repository;

import com.example.tradingbot.domain.Holding;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class HoldingRepository {
    private final JdbcTemplate jdbc;

    private static final RowMapper<Holding> ROW_MAPPER = new RowMapper<Holding>() {
        @Override
        public Holding mapRow(ResultSet rs, int rowNum) throws SQLException {
            Holding h = new Holding();
            h.setId(rs.getLong("id"));
            h.setSymbol(rs.getString("symbol"));
            h.setQuantity(rs.getBigDecimal("quantity"));
            h.setAvgCost(rs.getBigDecimal("avg_cost"));
            h.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
            return h;
        }
    };

    public HoldingRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Holding> findBySymbol(String symbol) {
        return jdbc.query("SELECT * FROM holdings WHERE symbol=?", ROW_MAPPER, symbol).stream().findFirst();
    }

    public List<Holding> findAll() {
        return jdbc.query("SELECT * FROM holdings ORDER BY symbol", ROW_MAPPER);
    }

    public void upsert(String symbol, BigDecimal qty, BigDecimal avgCost) {
        jdbc.update("INSERT INTO holdings(symbol, quantity, avg_cost) VALUES (?,?,?) ON CONFLICT(symbol) DO UPDATE SET quantity=EXCLUDED.quantity, avg_cost=EXCLUDED.avg_cost, updated_at=NOW()",
                symbol, qty, avgCost);
    }
}
