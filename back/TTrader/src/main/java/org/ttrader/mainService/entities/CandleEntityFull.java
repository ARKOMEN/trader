package org.ttrader.mainService.entities;

public interface CandleEntityFull extends CandleEntityShort {
    void setId(Long id);
    void setTicker(String ticker);
    void setPeriod(long period);
    String getTicker();
    Long getId();
    long getPeriod();
}
