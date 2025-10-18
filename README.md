# Automated Crypto Trading Bot Simulation

Full-stack demo with Java Spring Boot backend and vanilla JS frontend. Simulates a crypto trading bot operating in two modes: backtest over recent historical candles and live-sim using latest ticker price. Stores account, trades, and portfolio snapshots in a relational database via raw SQL (no ORM).

## Stack
- Backend: Java 17, Spring Boot 3, Web + JDBC
- DB: H2 (default, file-backed) with PostgreSQL-compatible dialect; switchable to Postgres via env vars
- Frontend: HTML/CSS/JS with Chart.js
- Market Data: Binance public REST API

## Quick Start
1. Backend
   - Requirements: Java 17+, Maven
   - Run:
     ```bash
     cd backend
     mvn spring-boot:run
     ```
   - Env overrides for Postgres (optional):
     ```bash
     export DB_URL=jdbc:postgresql://localhost:5432/trading
     export DB_USER=postgres
     export DB_PASSWORD=postgres
     export DB_DRIVER=org.postgresql.Driver
     mvn spring-boot:run
     ```
2. Frontend
   - Serve `frontend/` via any static server (or open `index.html` directly). If opening from file, you may need to allow CORS; backend already permits `*`.

## API Overview
- `POST /api/bot/backtest?symbol=BTCUSDT` – run one backtest pass on latest candles
- `POST /api/bot/live?symbol=BTCUSDT` – start live-sim ticking
- `POST /api/bot/pause` – pause bot
- `POST /api/bot/reset` – reset balances and state
- `GET /api/bot/status` – mode, symbol, account
- `GET /api/data/klines?symbol=BTCUSDT&interval=1m&limit=200` – latest candles
- `GET /api/data/trades?limit=100` – recent trades
- `GET /api/data/snapshots?limit=200` – recent portfolio value snapshots

## Database Schema
Raw SQL in `backend/src/main/resources/schema.sql`. Tables: `accounts`, `holdings`, `trades`, `portfolio_snapshots`.

## Trading Logic (SMA Crossover)
- Two simple moving averages: short=7, long=25 over close price.
- Buy 20% of cash when short > long by 0.1%.
- Sell 20% of holdings when short < long by 0.1%.
- Records trades and portfolio snapshots.

## Reflection
- Approach: Implemented SMA crossover for interpretability and simplicity; sufficient to demonstrate event-driven decisions in both backtest and live modes. Binance used for reliable public data.
- Design decisions: H2 default for zero-setup; Postgres-ready via env vars. Raw SQL via `JdbcTemplate` for transparency. Scheduled ticker drives live-sim; backtest runs a single pass per request then pauses to keep control deterministic from the UI.
- Trade-offs: Simplified PnL (no per-lot cost basis), approximate backtest speed, and single-symbol focus (BTC/ETH) for time. Error handling kept minimal.
- Tools/AI: Used an AI assistant for scaffolding boilerplate and wiring. Verified endpoints with manual testing and quick runs. Cross-checked Binance formats with docs.
- Next improvements: Real cost basis and PnL, multi-asset portfolio, order sizing with risk controls, unit tests, websocket market data, and a richer UI with trade markers on the price chart.

## Screenshots/Video
- Open the dashboard, start backtest or live-sim, and observe charts, trades, and balances.

## License
MIT
