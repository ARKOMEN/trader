package org.ttrader.mainService.entities;

public interface NewsShort {
    Long getId();
    TickerShort getTicker();
    String getTitle();
    String getDescription();
    String getUrl();
}
