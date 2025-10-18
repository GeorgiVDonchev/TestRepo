package com.example.tradingbot.repository;

import com.example.tradingbot.domain.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class AccountRepository {
    private final JdbcTemplate jdbc;

    private static final RowMapper<Account> ROW_MAPPER = new RowMapper<Account>() {
        @Override
        public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
            Account a = new Account();
            a.setId(rs.getLong("id"));
            a.setCashBalance(rs.getBigDecimal("cash_balance"));
            a.setCreatedAt(rs.getTimestamp("created_at").toInstant());
            a.setUpdatedAt(rs.getTimestamp("updated_at").toInstant());
            return a;
        }
    };

    public AccountRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Account> getDefault() {
        return jdbc.query("SELECT * FROM accounts WHERE id=1", ROW_MAPPER).stream().findFirst();
    }

    public void setCashBalance(BigDecimal amount) {
        jdbc.update("UPDATE accounts SET cash_balance=? WHERE id=1", amount);
    }

    public void ensureDefault(double initialCash) {
        jdbc.update("INSERT INTO accounts (id, cash_balance) VALUES (1, ?) ON CONFLICT (id) DO NOTHING", initialCash);
    }
}
