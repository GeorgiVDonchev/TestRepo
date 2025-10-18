-- Accounts table: single simulated account in USDT
CREATE TABLE IF NOT EXISTS accounts (
    id SERIAL PRIMARY KEY,
    base_currency VARCHAR(10) NOT NULL DEFAULT 'USDT',
    cash_balance NUMERIC(20,8) NOT NULL DEFAULT 10000.0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Holdings per symbol (e.g., BTC)
CREATE TABLE IF NOT EXISTS holdings (
    id SERIAL PRIMARY KEY,
    account_id INT NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    symbol VARCHAR(20) NOT NULL,
    quantity NUMERIC(20,8) NOT NULL DEFAULT 0,
    avg_cost NUMERIC(20,8) NOT NULL DEFAULT 0,
    UNIQUE(account_id, symbol)
);

-- Trades history
CREATE TABLE IF NOT EXISTS trades (
    id SERIAL PRIMARY KEY,
    trade_time TIMESTAMP NOT NULL,
    symbol VARCHAR(20) NOT NULL,
    side VARCHAR(4) NOT NULL CHECK (side IN ('BUY','SELL')),
    quantity NUMERIC(20,8) NOT NULL,
    price NUMERIC(20,8) NOT NULL,
    realized_pnl NUMERIC(20,8) NOT NULL DEFAULT 0
);

-- Portfolio value snapshots (for charts)
CREATE TABLE IF NOT EXISTS portfolio_snapshots (
    id SERIAL PRIMARY KEY,
    snapshot_time TIMESTAMP NOT NULL,
    total_value NUMERIC(20,8) NOT NULL
);

-- Ensure an account exists
INSERT INTO accounts (id)
SELECT 1
WHERE NOT EXISTS (SELECT 1 FROM accounts WHERE id = 1);

-- Ensure default holdings rows for popular symbols (optional)
INSERT INTO holdings (account_id, symbol, quantity)
SELECT 1, 'BTC', 0 WHERE NOT EXISTS (SELECT 1 FROM holdings WHERE account_id=1 AND symbol='BTC');
INSERT INTO holdings (account_id, symbol, quantity)
SELECT 1, 'ETH', 0 WHERE NOT EXISTS (SELECT 1 FROM holdings WHERE account_id=1 AND symbol='ETH');

-- Backfill schema for existing DBs
ALTER TABLE holdings ADD COLUMN IF NOT EXISTS avg_cost NUMERIC(20,8) NOT NULL DEFAULT 0;
