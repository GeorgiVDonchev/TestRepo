const apiBase = 'http://localhost:8080';

async function fetchJSON(path) {
  const res = await fetch(apiBase + path);
  if (!res.ok) throw new Error('HTTP ' + res.status);
  return await res.json();
}

async function post(path, params = {}) {
  const url = new URL(apiBase + path);
  Object.entries(params).forEach(([k, v]) => url.searchParams.set(k, v));
  const res = await fetch(url.toString(), { method: 'POST' });
  if (!res.ok) throw new Error('HTTP ' + res.status);
  return await res.json();
}

function setStatus(mode, cash) {
  document.getElementById('mode').textContent = 'Mode: ' + mode;
  document.getElementById('balance').textContent = 'Cash: ' + Number(cash).toFixed(2);
}

async function refresh() {
  try {
    const status = await fetchJSON('/api/bot/status');
    setStatus(status.mode, status.account.cash_balance);

    const symbol = document.getElementById('symbol').value;
    const klines = await fetchJSON(`/api/data/klines?symbol=${symbol}&interval=1m&limit=120`);
    const priceLabels = klines.map(k => new Date(k.closeTime).toLocaleTimeString());
    const prices = klines.map(k => k.close);
    window.updatePriceChart(priceLabels, prices);

    const snapshots = await fetchJSON('/api/data/snapshots?limit=120');
    const snapLabels = snapshots.map(s => new Date(s.snapshot_time).toLocaleTimeString());
    const values = snapshots.map(s => s.total_value);
    window.updatePortfolioChart(snapLabels.reverse(), values.reverse());

    const trades = await fetchJSON('/api/data/trades?limit=100');
    renderTrades(trades);
    window.updateTradeMarkers(priceLabels, trades);
  } catch (e) {
    console.error(e);
  }
}

function renderTrades(trades) {
  const tbody = document.querySelector('#tradesTable tbody');
  tbody.innerHTML = '';
  trades.reverse().forEach(t => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${new Date(t.trade_time).toLocaleString()}</td>
      <td>${t.side}</td>
      <td>${t.symbol}</td>
      <td>${Number(t.quantity).toFixed(6)}</td>
      <td>${Number(t.price).toFixed(2)}</td>
      <td>${Number(t.realized_pnl).toFixed(2)}</td>
    `;
    tbody.appendChild(tr);
  });
}

function bindControls() {
  document.getElementById('startBacktest').addEventListener('click', async () => {
    const symbol = document.getElementById('symbol').value;
    await post('/api/bot/backtest', { symbol });
    await refresh();
  });
  document.getElementById('startLive').addEventListener('click', async () => {
    const symbol = document.getElementById('symbol').value;
    await post('/api/bot/live', { symbol });
    await refresh();
  });
  document.getElementById('pause').addEventListener('click', async () => {
    await post('/api/bot/pause');
    await refresh();
  });
  document.getElementById('reset').addEventListener('click', async () => {
    await post('/api/bot/reset');
    await refresh();
  });
}

window.addEventListener('DOMContentLoaded', async () => {
  window.initCharts();
  bindControls();
  await refresh();
  setInterval(refresh, 4000);
});
