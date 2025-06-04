package org.ttrader.mainService.entities;

import jakarta.persistence.*;

@Entity
public class NewsEntity implements NewsShort {
    @Id
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private long time;

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

    public NewsEntity(Long id, long time, TickerEntity ticker, String title, String description, String url) {
        this.id = id;
        this.time = time;
        this.ticker = ticker;
        this.title = title;
        this.description = description;
        this.url = url;
    }

    public NewsEntity(long time, TickerEntity ticker, String title, String description, String url) {
        this.time = time;
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
    public long getTime() { return time; }
    public void setId(Long id) { this.id = id; }
    public void setTicker(TickerEntity ticker) { this.ticker = ticker; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setUrl(String url) { this.url = url; }
    public void setTime(long time) { this.time = time; }
}
