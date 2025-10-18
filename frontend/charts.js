let priceChart, portfolioChart;

function initCharts() {
  const priceCtx = document.getElementById('priceChart').getContext('2d');
  priceChart = new Chart(priceCtx, {
    type: 'line',
    data: { labels: [], datasets: [
      { label: 'Price', data: [], borderColor: '#22d3ee', backgroundColor: 'rgba(34,211,238,0.15)', tension: 0.2 },
      { label: 'Buys', data: [], borderColor: '#10b981', pointBackgroundColor: '#10b981', showLine: false, pointRadius: 5, pointStyle: 'triangle' },
      { label: 'Sells', data: [], borderColor: '#ef4444', pointBackgroundColor: '#ef4444', showLine: false, pointRadius: 5, pointStyle: 'rectRot' },
    ]},
    options: { responsive: true, plugins: { legend: { labels: { color: '#e2e8f0' } } }, scales: { x: { ticks: { color: '#94a3b8' } }, y: { ticks: { color: '#94a3b8' } } } }
  });

  const portfolioCtx = document.getElementById('portfolioChart').getContext('2d');
  portfolioChart = new Chart(portfolioCtx, {
    type: 'line',
    data: { labels: [], datasets: [
      { label: 'Portfolio Value', data: [], borderColor: '#a3e635', backgroundColor: 'rgba(163,230,53,0.15)', tension: 0.2 },
    ]},
    options: { responsive: true, plugins: { legend: { labels: { color: '#e2e8f0' } } }, scales: { x: { ticks: { color: '#94a3b8' } }, y: { ticks: { color: '#94a3b8' } } } }
  });
}

function updatePriceChart(labels, prices) {
  priceChart.data.labels = labels;
  priceChart.data.datasets[0].data = prices;
  priceChart.update();
}

function updatePortfolioChart(labels, values) {
  portfolioChart.data.labels = labels;
  portfolioChart.data.datasets[0].data = values;
  portfolioChart.update();
}

function updateTradeMarkers(labels, trades) {
  if (!priceChart) return;
  const buys = new Array(labels.length).fill(null);
  const sells = new Array(labels.length).fill(null);
  const labelIndex = new Map();
  labels.forEach((l, i) => labelIndex.set(l, i));
  (trades || []).forEach(t => {
    const timeLabel = new Date(t.trade_time).toLocaleTimeString();
    const idx = labelIndex.get(timeLabel);
    if (idx !== undefined) {
      if (t.side === 'BUY') buys[idx] = t.price;
      else if (t.side === 'SELL') sells[idx] = t.price;
    }
  });
  priceChart.data.datasets[1].data = buys;
  priceChart.data.datasets[2].data = sells;
  priceChart.update();
}

// Expose to global scope (no ESM)
window.initCharts = initCharts;
window.updatePriceChart = updatePriceChart;
window.updatePortfolioChart = updatePortfolioChart;
window.updateTradeMarkers = updateTradeMarkers;
