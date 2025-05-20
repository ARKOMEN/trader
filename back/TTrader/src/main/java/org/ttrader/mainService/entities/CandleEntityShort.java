package org.ttrader.mainService.entities;

public interface CandleEntityShort {
    void setOpen(double open);
    void setHigh(double high);
    void setLow(double low);
    void setClose(double close);
    void setTimestamp(long timestamp);
    double getOpen();
    double getHigh();
    double getLow();
    double getClose();
    long getTimestamp();
}
