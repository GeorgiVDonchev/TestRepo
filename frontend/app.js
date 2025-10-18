const API = 'http://localhost:8080/api';
let liveIntervalId = null;

const priceChartCtx = document.getElementById('priceChart').getContext('2d');
const equityChartCtx = document.getElementById('equityChart').getContext('2d');
const priceChart = new Chart(priceChartCtx, { type: 'line', data: { labels: [], datasets: [{ label: 'Price', data: [], borderColor: '#7dd3fc', fill: false }] }, options: { scales: { x: { display: false } } }});
const equityChart = new Chart(equityChartCtx, { type: 'line', data: { labels: [], datasets: [{ label: 'Equity', data: [], borderColor: '#86efac', fill: false }] }, options: { scales: { x: { display: false } } }});

async function train() {
  const body = { symbol: 'BTCUSDT', interval: '1m', shortSma: 20, longSma: 50, lookback: 500 };
  const res = await fetch(`${API}/bot/train`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) });
  const data = await res.json();
  document.getElementById('return').innerText = `${data.returnPct.toFixed(2)}%`;
}

async function stepLive() {
  await fetch(`${API}/bot/live/step`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ symbol: 'BTCUSDT' })});
  await refreshStatus();
}

async function reset() {
  await fetch(`${API}/bot/reset`, { method: 'POST' });
  await refreshStatus();
}

async function refreshStatus() {
  const res = await fetch(`${API}/bot/status`);
  const data = await res.json();
  document.getElementById('cash').innerText = Number(data.account.cashBalance).toFixed(2);
  const holdingQty = data.holding ? Number(data.holding.quantity) : 0;
  document.getElementById('holding').innerText = holdingQty.toFixed(6);
  const tbody = document.querySelector('#trades tbody');
  tbody.innerHTML = '';
  data.trades.forEach(t => {
    const tr = document.createElement('tr');
    tr.innerHTML = `<td>${new Date(t.timestamp).toLocaleString()}</td><td>${t.side}</td><td>${t.quantity}</td><td>${t.price}</td><td>${t.fee}</td><td>${t.realizedPnl}</td>`;
    tbody.appendChild(tr);
  });
}

function start() {
  const mode = document.getElementById('mode').value;
  if (mode === 'train') {
    train();
  } else {
    if (liveIntervalId) return;
    liveIntervalId = setInterval(stepLive, 5000);
  }
}

function pause() {
  if (liveIntervalId) {
    clearInterval(liveIntervalId);
    liveIntervalId = null;
  }
}

function step() { stepLive(); }

function initControls() {
  document.getElementById('start').addEventListener('click', start);
  document.getElementById('pause').addEventListener('click', pause);
  document.getElementById('step').addEventListener('click', step);
  document.getElementById('reset').addEventListener('click', reset);
}

initControls();
refreshStatus();
