package org.ttrader.mainService.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class TickerEntity {
    @Id
    @Column(nullable = false, length = 10)
    private String ticker;

    @OneToMany(mappedBy = "ticker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NewsEntity> newsEntityList;

    @OneToMany(mappedBy = "ticker", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CandleEntity> candleEntityList;

    //...
    public TickerEntity(){}
    public TickerEntity(String ticker) {
        this.ticker = ticker;
    }
    public String getTicker() { return ticker; }
    public void setTicker(String ticker) { this.ticker = ticker; }
}
