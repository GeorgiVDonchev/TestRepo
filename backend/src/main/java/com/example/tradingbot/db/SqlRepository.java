package com.example.tradingbot.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Repository
public class SqlRepository {
    private final JdbcTemplate jdbc;

    public SqlRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Map<String, Object> getAccount() {
        return jdbc.queryForMap("SELECT id, base_currency, cash_balance FROM accounts WHERE id=1");
    }

    public void updateCashBalance(double delta) {
        jdbc.update("UPDATE accounts SET cash_balance = cash_balance + ? WHERE id=1", delta);
    }

    public double getHolding(String symbol) {
        Double qty = jdbc.query("SELECT quantity FROM holdings WHERE account_id=1 AND symbol=?", ps -> ps.setString(1, symbol), (ResultSet rs) -> {
            if (rs.next()) return rs.getDouble(1);
            return 0.0;
        });
        return qty != null ? qty : 0.0;
    }

    public void setHolding(String symbol, double quantity) {
        // Compatible with H2 (no ON CONFLICT) and Postgres
        int updated = jdbc.update("UPDATE holdings SET quantity=? WHERE account_id=1 AND symbol=?", quantity, symbol);
        if (updated == 0) {
            jdbc.update("INSERT INTO holdings (account_id, symbol, quantity) VALUES (1, ?, ?)", symbol, quantity);
        }
    }

    public void clearTradesAndSnapshots() {
        jdbc.update("DELETE FROM trades");
        jdbc.update("DELETE FROM portfolio_snapshots");
    }

    public void insertTrade(String symbol, String side, double quantity, double price, double realizedPnl) {
        jdbc.update("INSERT INTO trades (trade_time, symbol, side, quantity, price, realized_pnl) VALUES (TO_TIMESTAMP(?/1000.0), ?, ?, ?, ?, ?)",
                Instant.now().toEpochMilli(), symbol, side, quantity, price, realizedPnl);
    }

    public List<Map<String, Object>> listTrades(int limit) {
        return jdbc.queryForList("SELECT trade_time, symbol, side, quantity, price, realized_pnl FROM trades ORDER BY trade_time DESC LIMIT ?", limit);
    }

    public void insertSnapshot(double totalValue) {
        jdbc.update("INSERT INTO portfolio_snapshots (snapshot_time, total_value) VALUES (NOW(), ?)", totalValue);
    }

    public List<Map<String, Object>> listSnapshots(int limit) {
        return jdbc.queryForList("SELECT snapshot_time, total_value FROM portfolio_snapshots ORDER BY snapshot_time DESC LIMIT ?", limit);
    }
}
