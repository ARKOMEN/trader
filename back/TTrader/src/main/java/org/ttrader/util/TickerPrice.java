package org.ttrader.util;

public record TickerPrice(
    String ticker,
    double price,
    long timestamp
) {
}
