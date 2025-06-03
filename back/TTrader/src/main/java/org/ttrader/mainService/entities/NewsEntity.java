package org.ttrader.mainService.entities;

import jakarta.persistence.*;

@Entity
public class NewsEntity {
    @Id
    @Column(nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticker", nullable = false)
    private TickerEntity ticker;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255)
    private String description;
    @Column(nullable = false, length = 255)
    private String url;

    public NewsEntity() {}

    public NewsEntity(Long id, TickerEntity ticker, String title, String description, String url) {
        this.id = id;
        this.ticker = ticker;
        this.title = title;
        this.description = description;
        this.url = url;
    }

    public NewsEntity(TickerEntity ticker, String title, String description, String url) {
        this.ticker = ticker;
        this.title = title;
        this.description = description;
        this.url = url;
    }

    public Long getId() { return id; }
    public TickerEntity getTicker() { return ticker; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getUrl() { return url; }
    public void setId(Long id) { this.id = id; }
    public void setTicker(TickerEntity ticker) { this.ticker = ticker; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setUrl(String url) { this.url = url; }
}
