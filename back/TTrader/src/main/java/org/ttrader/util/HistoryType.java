package org.ttrader.util;

public record HistoryType(
    GraphType graphType,
    long graphInterval,
    CandlePeriod candlePeriod
) {

}
