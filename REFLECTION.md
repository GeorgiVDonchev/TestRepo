# Reflection

This project demonstrates an automated crypto trading bot with two modes—training (backtest) and live-sim—implemented with a Java Spring Boot backend, a vanilla JS frontend, raw SQL persistence, and Binance public data.

## Key design decisions
- Trading logic: I used a simple moving-average (SMA) crossover (short=7, long=25) because it’s easy to reason about, deterministic for backtests, and sufficient to showcase autonomous decisions. A small threshold (±0.1%) reduces churn around the crossover.
- Modes: Backtest processes a recent candle window (default 200 x 1m candles) in a single pass per request, then pauses. This keeps user control deterministic from the UI and avoids unbounded loops. Live-sim uses a scheduled poll (default 5s) to fetch the latest price and decide.
- Persistence: Raw SQL via JdbcTemplate (no ORM), with `accounts`, `holdings`, `trades`, and `portfolio_snapshots`. Default DB is file-backed H2 in PostgreSQL mode for zero setup; swapping to Postgres is done via environment variables without code changes.
- Market data: Binance REST for reliability and public access. Klines power backtests, the ticker powers live-sim. Network failures fall back safely (no trades performed when prices are unavailable).
- Frontend: Minimal, responsive dashboard with controls (start backtest, start live, pause, reset), price chart with trade markers, portfolio value chart, and a trade history table.

## Outcomes
- Both modes function end-to-end: market data → strategy decision → trades/snapshots → UI.
- Accurate account and holdings updates on each trade; portfolio value reflects cash + mark-to-market of holdings.
- Clear UI for status, recent trades, price, and portfolio evolution.

## Trade-offs and shortcuts
- PnL is approximated (no per-lot cost basis/realized PnL calc).
- Single-symbol focus (BTC/ETH examples) to keep scope tight.
- REST polling instead of websockets; sufficient for a demo but not production-grade latency.
- Limited error handling and retry logic (kept simple for clarity).

## Tools and AI usage
- Used an AI assistant to scaffold the project structure, boilerplate wiring, and documentation text. I asked for a full-stack implementation outline and then iterated on endpoints, SQL schema, and UI wiring. I verified endpoints manually and by reading code paths, and I cross-checked Binance payload formats.

## If I had more time
- Robust cost basis tracking and accurate realized/unrealized PnL.
- Position sizing and risk controls (max drawdown, stop-loss, volatility scaling).
- Multiple simultaneous symbols and UI selection.
- Websocket market data and candle aggregation.
- Deeper testing: unit tests for strategy and services, and a simple integration test against a test DB.
