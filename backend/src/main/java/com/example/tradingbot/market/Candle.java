package com.example.tradingbot.market;

public class Candle {
    private long openTime;
    private double open;
    private double high;
    private double low;
    private double close;
    private long closeTime;
    private double volume;

    public Candle() {}

    public Candle(long openTime, double open, double high, double low, double close, long closeTime, double volume) {
        this.openTime = openTime;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.closeTime = closeTime;
        this.volume = volume;
    }

    public long getOpenTime() { return openTime; }
    public void setOpenTime(long openTime) { this.openTime = openTime; }

    public double getOpen() { return open; }
    public void setOpen(double open) { this.open = open; }

    public double getHigh() { return high; }
    public void setHigh(double high) { this.high = high; }

    public double getLow() { return low; }
    public void setLow(double low) { this.low = low; }

    public double getClose() { return close; }
    public void setClose(double close) { this.close = close; }

    public long getCloseTime() { return closeTime; }
    public void setCloseTime(long closeTime) { this.closeTime = closeTime; }

    public double getVolume() { return volume; }
    public void setVolume(double volume) { this.volume = volume; }
}
