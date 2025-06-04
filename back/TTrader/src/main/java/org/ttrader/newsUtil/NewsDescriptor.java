package org.ttrader.newsUtil;

public record NewsDescriptor(
    String ticker,
    String title,
    String description,
    String url,
    long id,
    long timestamp
) {
}
