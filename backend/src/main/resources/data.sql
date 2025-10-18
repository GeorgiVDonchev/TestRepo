INSERT INTO accounts (id, cash_balance)
VALUES (1, 10000.00)
ON CONFLICT (id) DO UPDATE SET cash_balance = EXCLUDED.cash_balance;
