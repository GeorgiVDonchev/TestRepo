package com.example.tradingbot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.trading")
public class AppProperties {
    private double initialCash;
    private int feeRateBps;
    private int livePollSeconds;
    private String defaultSymbol;
    private String defaultInterval;

    public double getInitialCash() { return initialCash; }
    public void setInitialCash(double initialCash) { this.initialCash = initialCash; }
    public int getFeeRateBps() { return feeRateBps; }
    public void setFeeRateBps(int feeRateBps) { this.feeRateBps = feeRateBps; }
    public int getLivePollSeconds() { return livePollSeconds; }
    public void setLivePollSeconds(int livePollSeconds) { this.livePollSeconds = livePollSeconds; }
    public String getDefaultSymbol() { return defaultSymbol; }
    public void setDefaultSymbol(String defaultSymbol) { this.defaultSymbol = defaultSymbol; }
    public String getDefaultInterval() { return defaultInterval; }
    public void setDefaultInterval(String defaultInterval) { this.defaultInterval = defaultInterval; }
}
