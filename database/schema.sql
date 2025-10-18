-- Crypto Trading Bot Database Schema

-- Create database (uncomment if needed)
-- CREATE DATABASE crypto_trading_bot;
-- USE crypto_trading_bot;

-- Account table to track balance
CREATE TABLE IF NOT EXISTS account (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    balance DECIMAL(20, 8) NOT NULL DEFAULT 10000.00,
    initial_balance DECIMAL(20, 8) NOT NULL DEFAULT 10000.00,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Portfolio holdings table
CREATE TABLE IF NOT EXISTS portfolio (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    symbol VARCHAR(20) NOT NULL,
    quantity DECIMAL(20, 8) NOT NULL DEFAULT 0,
    average_buy_price DECIMAL(20, 8) NOT NULL DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_symbol (symbol)
);

-- Trade history table
CREATE TABLE IF NOT EXISTS trade_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    symbol VARCHAR(20) NOT NULL,
    action VARCHAR(10) NOT NULL, -- BUY or SELL
    quantity DECIMAL(20, 8) NOT NULL,
    price DECIMAL(20, 8) NOT NULL,
    total_value DECIMAL(20, 8) NOT NULL,
    profit_loss DECIMAL(20, 8) DEFAULT 0,
    balance_after DECIMAL(20, 8) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mode VARCHAR(20) NOT NULL, -- TRAINING or TRADING
    strategy_reason TEXT,
    INDEX idx_symbol (symbol),
    INDEX idx_timestamp (timestamp),
    INDEX idx_mode (mode)
);

-- Price history table for training mode
CREATE TABLE IF NOT EXISTS price_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    symbol VARCHAR(20) NOT NULL,
    price DECIMAL(20, 8) NOT NULL,
    volume DECIMAL(20, 2),
    market_cap DECIMAL(30, 2),
    timestamp TIMESTAMP NOT NULL,
    INDEX idx_symbol_timestamp (symbol, timestamp)
);

-- Bot configuration table
CREATE TABLE IF NOT EXISTS bot_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    mode VARCHAR(20) NOT NULL, -- TRAINING or TRADING
    status VARCHAR(20) NOT NULL, -- RUNNING, PAUSED, STOPPED
    symbol VARCHAR(20) NOT NULL DEFAULT 'bitcoin',
    interval_seconds INT NOT NULL DEFAULT 60,
    strategy VARCHAR(50) NOT NULL DEFAULT 'MOVING_AVERAGE',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Initialize default account with $10,000
INSERT INTO account (balance, initial_balance) 
VALUES (10000.00, 10000.00)
ON DUPLICATE KEY UPDATE balance = balance;

-- Initialize default bot config
INSERT INTO bot_config (mode, status, symbol, interval_seconds, strategy) 
VALUES ('TRADING', 'STOPPED', 'bitcoin', 60, 'MOVING_AVERAGE')
ON DUPLICATE KEY UPDATE mode = mode;
