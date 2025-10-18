# Automated Crypto Trading Bot Simulator

A simple full-stack project: Spring Boot backend (Java 17) with raw SQL (no ORM) and a vanilla JS frontend that visualizes an SMA-crossover trading bot in training (backtest) and live-sim modes.

## Stack
- Backend: Spring Boot 3, WebFlux `WebClient`, JDBC (no ORM)
- DB: PostgreSQL
- Frontend: HTML/CSS/Vanilla JS + Chart.js

## Data Source
- Binance public REST API: `https://api.binance.com` for klines and ticker price.

## Quick Start (Docker)
```bash
# 1) Start PostgreSQL and backend
docker compose up --build -d

# 2) Open the frontend
# Open frontend/index.html in a browser (served by your editor's Live Server or a static server)
# If using a static server, ensure CORS is allowed (backend allows all origins).
```

Env defaults used by backend:
- `DB_HOST=db`, `DB_PORT=5432`, `DB_NAME=tradingbot`, `DB_USER=tradingbot`, `DB_PASSWORD=tradingbot`

## Development (without Docker)
- Install PostgreSQL locally, create db and user matching `application.yml` env defaults
- Run backend:
```bash
# Requires Java 17 and Maven
mvn -f backend/pom.xml spring-boot:run
```
- Open `frontend/index.html`.

## API
- `POST /api/bot/train` body: `{ symbol, interval, shortSma, longSma, lookback }` -> `{ trades, returnPct }`
- `POST /api/bot/live/step` body: `{ symbol }` -> `{ status: "ok" }`
- `POST /api/bot/reset` -> resets cash and holding
- `GET /api/bot/status` -> account, current holding and recent trades
- `GET /api/data/klines?symbol=BTCUSDT&interval=1m&limit=200` -> recent klines
- `GET /api/data/portfolio?limit=200` -> portfolio equity curve

## Database Schema (raw SQL)
- `schema.sql` and `data.sql` are executed automatically on startup (Spring SQL init).
- Tables: `accounts`, `holdings`, `trades`, `portfolio_value_history`.

## Trading Logic
- SMA crossover: buy when short SMA crosses above long SMA; sell when below.
- Fee model: `feeRateBps` basis points.

## Screens / Screenshots to Provide
- Dashboard controls and stats (frontend)
- Price chart and equity curve
- Trade history table
- Short video showing training and live steps

## Notes
- This is a simulation. No real orders are placed.
- The bot uses Binance public endpoints without authentication.
