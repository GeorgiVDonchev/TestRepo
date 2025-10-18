package com.example.tradingbot.market;

import java.time.Instant;

public class Kline {
    private Instant openTime;
    private double open;
    private double high;
    private double low;
    private double close;
    private double volume;
    private Instant closeTime;

    public Instant getOpenTime() { return openTime; }
    public void setOpenTime(Instant openTime) { this.openTime = openTime; }
    public double getOpen() { return open; }
    public void setOpen(double open) { this.open = open; }
    public double getHigh() { return high; }
    public void setHigh(double high) { this.high = high; }
    public double getLow() { return low; }
    public void setLow(double low) { this.low = low; }
    public double getClose() { return close; }
    public void setClose(double close) { this.close = close; }
    public double getVolume() { return volume; }
    public void setVolume(double volume) { this.volume = volume; }
    public Instant getCloseTime() { return closeTime; }
    public void setCloseTime(Instant closeTime) { this.closeTime = closeTime; }
}
