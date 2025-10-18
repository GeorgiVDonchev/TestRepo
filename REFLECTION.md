This solution delivers a minimal, end‑to‑end crypto bot simulator with two modes: backtest (training) and live simulation. The backend is Spring Boot (Java 17) using raw SQL via JdbcTemplate (no ORM), with schema and seed data provided in `schema.sql` and `data.sql`. The frontend is a small vanilla JS dashboard using Chart.js.

Key design decisions
- Trading logic: a simple SMA crossover (e.g., 20/50) for clarity and determinism. It’s widely understood, quick to implement, and sufficient to demonstrate the full stack. Backtests compute equity from initial cash; live-sim runs a single-step loop triggered via API, enabling manual stepping or polling.
- Market data: Binance public REST API for klines and ticker price to avoid credentials and keep latency reasonable.
- Persistence: Raw SQL with `JdbcTemplate` to comply with the no-ORM constraint. Repositories encapsulate SQL for accounts, holdings, trades, and portfolio value snapshots.
- Architecture: Thin controllers -> services (trading, portfolio) -> repositories. The `MarketDataClient` abstracts external calls. `AppProperties` centralizes bot settings.

Trade-offs and shortcuts
- Strategy is intentionally simple; no portfolio of multiple symbols, no risk management (stop loss/position sizing beyond all-in), and no slippage model beyond a flat fee.
- Live mode is a push-button step rather than a background scheduler; a cron/worker would be cleaner.
- Error handling is basic; retries/circuit breaking for HTTP calls could be added.
- Frontend uses vanilla JS for speed; a React/Vue app would structure state/views better.

External tools and AI
- Used an AI assistant to scaffold files and outline components. Prompts focused on project setup, raw SQL repositories, API endpoints, and simple SMA strategy. Verified output by reviewing generated code and ensuring endpoints and schema align. Docker Compose is provided for reproducible DB.

Improvements with more time
- Add multi-asset support and position sizing/risk constraints; configurable strategies (EMA/RSI/MACD) and optimization.
- Replace polling with a scheduled job and WebSocket push to the UI; add historical equity chart sourced from DB alongside trade markers on price chart.
- Expand tests: unit tests for indicators and services; integration tests with testcontainers for PostgreSQL.
- Harden resilience (timeouts, retries, rate limits) and add metrics/observability.
