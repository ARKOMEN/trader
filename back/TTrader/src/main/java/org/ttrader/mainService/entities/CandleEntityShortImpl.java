package org.ttrader.mainService.entities;

public class CandleEntityShortImpl implements CandleEntityShort {

    private double open, low, high, close;
    private long timestamp;

    public CandleEntityShortImpl(double open, double low, double high, double close, long timestamp) {
        this.open = open;
        this.low = low;
        this.high = high;
        this.close = close;
        this.timestamp = timestamp;
    }

    @Override
    public void setOpen(double open) { this.open = open; }
    @Override
    public void setHigh(double high) { this.high = high; }
    @Override
    public void setLow(double low) { this.low = low; }
    @Override
    public void setClose(double close) { this.close = close; }
    @Override
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    @Override
    public double getOpen() { return open; }
    @Override
    public double getHigh() { return high; }
    @Override
    public double getLow() { return low; }
    @Override
    public double getClose() { return close; }
    @Override
    public long getTimestamp() { return timestamp; }
}
