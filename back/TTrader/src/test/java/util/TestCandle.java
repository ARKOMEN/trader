package util;

import org.ttrader.mainService.entities.CandleEntityShort;

public record TestCandle (
    double open, double high, double low, double close, long timestamp
)implements CandleEntityShort {

    public TestCandle(long price, long timestamp) {
        this(price, price, price, price, timestamp);
    }

    @Override
    public void setOpen(double open) {}
    @Override
    public void setHigh(double high) {}
    @Override
    public void setLow(double low) {}
    @Override
    public void setClose(double close) {}
    @Override
    public void setTimestamp(long timestamp) {}

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
