package com.cryptobot.repository;

import com.cryptobot.model.BotConfig;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class BotConfigRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public BotConfigRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private final RowMapper<BotConfig> configRowMapper = new RowMapper<BotConfig>() {
        @Override
        public BotConfig mapRow(ResultSet rs, int rowNum) throws SQLException {
            BotConfig config = new BotConfig();
            config.setId(rs.getLong("id"));
            config.setMode(rs.getString("mode"));
            config.setStatus(rs.getString("status"));
            config.setSymbol(rs.getString("symbol"));
            config.setIntervalSeconds(rs.getInt("interval_seconds"));
            config.setStrategy(rs.getString("strategy"));
            config.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return config;
        }
    };
    
    public BotConfig getConfig() {
        String sql = "SELECT * FROM bot_config LIMIT 1";
        List<BotConfig> configs = jdbcTemplate.query(sql, configRowMapper);
        return configs.isEmpty() ? null : configs.get(0);
    }
    
    public void updateConfig(BotConfig config) {
        String sql = "UPDATE bot_config SET mode = ?, status = ?, symbol = ?, " +
                    "interval_seconds = ?, strategy = ? WHERE id = 1";
        jdbcTemplate.update(sql,
                config.getMode(),
                config.getStatus(),
                config.getSymbol(),
                config.getIntervalSeconds(),
                config.getStrategy()
        );
    }
    
    public void updateStatus(String status) {
        String sql = "UPDATE bot_config SET status = ? WHERE id = 1";
        jdbcTemplate.update(sql, status);
    }
    
    public void updateMode(String mode) {
        String sql = "UPDATE bot_config SET mode = ? WHERE id = 1";
        jdbcTemplate.update(sql, mode);
    }
}
