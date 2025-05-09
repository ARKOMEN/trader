package org.ttrader.mainService.entities;

public interface CandleEntityFull extends CandleEntityShort {
    void setTicker(String ticker);
    String getTicker();
}
