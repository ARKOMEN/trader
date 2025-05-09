package org.ttrader.mainService.entities;

public interface CandleEntityShort {
    void setId(Long id);
    void setOpen(double open);
    void setHigh(double high);
    void setLow(double low);
    void setClose(double close);
    void setTimestamp(long timestamp);
    void setPeriod(long period);
    Long getId();
    double getOpen();
    double getHigh();
    double getLow();
    double getClose();
    long getTimestamp();
    long getPeriod();
}
