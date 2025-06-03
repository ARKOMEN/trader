package org.ttrader.mainService.entities;


import org.ttrader.util.CandlePeriod;
import org.ttrader.util.TickerPrice;

public class CandleFunny implements CandleEntityFull {
    private Long id;
    private String ticker;
    private double open;//cost at the beginning of a candle period
    private double high;//highest cost in a candle period
    private double low;//lowest cost in a candle period
    private double close;//cost at the ending of a candle period
    private long timestamp; // UNIX seconds
    private long period; // UNIX seconds
    public CandleFunny() {}
    public CandleFunny(
        String ticker,
        double open, double high, double low, double close,
        long timestamp, long period
    ) {
        this.ticker = ticker;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.timestamp = timestamp;
        this.period = period;
    }

    public CandleFunny(String ticker, double price, long timestamp, long period) {
        this.ticker = ticker;
        this.open = price;
        this.high = price;
        this.low = price;
        this.close = price;
        this.timestamp = timestamp;
        this.period = period;
    }

    public CandleFunny(CandleEntityFull candle) {
        this.ticker = candle.getTicker();
        this.id = candle.getId();
        this.low = candle.getLow();
        this.high = candle.getHigh();
        this.open = candle.getOpen();
        this.close = candle.getClose();
        this.timestamp = candle.getTimestamp();
        this.period = candle.getPeriod();
    }

    public CandleFunny(TickerPrice tickerPrice, CandlePeriod candlePeriod) {
        this(
            tickerPrice.ticker(),
            tickerPrice.price(),
            candlePeriod.normalize(tickerPrice.timestamp()),
            candlePeriod.getUnixPeriod()
        );
    }

    public CandleFunny(TickerPrice tickerPrice, long candlePeriod) {
        this(
            tickerPrice.ticker(),
            tickerPrice.price(),
            CandlePeriod.normalize(tickerPrice.timestamp(), candlePeriod),
            candlePeriod
        );
    }

    @Override
    public void setId(Long id) { this.id = id; }
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
    public void setPeriod(long period) { this.period = period; }

    @Override
    public void setTicker(String ticker) { this.ticker = ticker; }

    @Override
    public Long getId() { return id; }
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
    @Override
    public long getPeriod() { return period; }

    @Override
    public String getTicker() { return ticker; }
}
