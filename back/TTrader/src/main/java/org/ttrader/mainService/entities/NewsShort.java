package org.ttrader.mainService.entities;

public interface NewsShort {
    Long getId();
    String getTicker();
    String getTitle();
    String getDescription();
    String getUrl();
}
