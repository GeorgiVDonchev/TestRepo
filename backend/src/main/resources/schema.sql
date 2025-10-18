-- Schema for trading bot
CREATE TABLE IF NOT EXISTS accounts (
    id SERIAL PRIMARY KEY,
    cash_balance NUMERIC(18,8) NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS holdings (
    id SERIAL PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL UNIQUE,
    quantity NUMERIC(28,12) NOT NULL DEFAULT 0,
    avg_cost NUMERIC(18,8) NOT NULL DEFAULT 0,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'trade_side') THEN
        CREATE TYPE trade_side AS ENUM ('BUY', 'SELL');
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS trades (
    id SERIAL PRIMARY KEY,
    ts TIMESTAMPTZ NOT NULL,
    symbol VARCHAR(20) NOT NULL,
    side trade_side NOT NULL,
    quantity NUMERIC(28,12) NOT NULL,
    price NUMERIC(18,8) NOT NULL,
    fee NUMERIC(18,8) NOT NULL DEFAULT 0,
    realized_pnl NUMERIC(18,8) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS portfolio_value_history (
    id SERIAL PRIMARY KEY,
    ts TIMESTAMPTZ NOT NULL,
    total_value NUMERIC(18,8) NOT NULL,
    cash NUMERIC(18,8) NOT NULL,
    holdings_value NUMERIC(18,8) NOT NULL
);

-- triggers to keep updated_at
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'set_updated_at_accounts') THEN
    CREATE TRIGGER set_updated_at_accounts BEFORE UPDATE ON accounts
      FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;

DO $$ BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_trigger WHERE tgname = 'set_updated_at_holdings') THEN
    CREATE TRIGGER set_updated_at_holdings BEFORE UPDATE ON holdings
      FOR EACH ROW EXECUTE FUNCTION set_updated_at();
  END IF;
END $$;
