package com.cryptobot.repository;

import com.cryptobot.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class AccountRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public AccountRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private final RowMapper<Account> accountRowMapper = new RowMapper<Account>() {
        @Override
        public Account mapRow(ResultSet rs, int rowNum) throws SQLException {
            Account account = new Account();
            account.setId(rs.getLong("id"));
            account.setBalance(rs.getBigDecimal("balance"));
            account.setInitialBalance(rs.getBigDecimal("initial_balance"));
            account.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            account.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            return account;
        }
    };
    
    public Account getAccount() {
        String sql = "SELECT * FROM account LIMIT 1";
        List<Account> accounts = jdbcTemplate.query(sql, accountRowMapper);
        return accounts.isEmpty() ? null : accounts.get(0);
    }
    
    public void updateBalance(BigDecimal newBalance) {
        String sql = "UPDATE account SET balance = ? WHERE id = 1";
        jdbcTemplate.update(sql, newBalance);
    }
    
    public void resetAccount() {
        String sql = "UPDATE account SET balance = initial_balance WHERE id = 1";
        jdbcTemplate.update(sql);
    }
}
