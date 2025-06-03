package org.ttrader.analysisUtil;

public record AnalysisCandle(
    double open, double high, double low, double close, long timeStamp
) {
}
