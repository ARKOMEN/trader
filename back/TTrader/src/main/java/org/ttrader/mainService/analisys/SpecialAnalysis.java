package org.ttrader.mainService.analisys;

import org.ttrader.analysisUtil.AnalysisAction;
import org.ttrader.analysisUtil.CommonAnalysis;

public record SpecialAnalysis(
    String ticker, AnalysisAction analysisAction, int confidence
) {
    public SpecialAnalysis(String ticker, CommonAnalysis commonAnalysis) {
        this(ticker, commonAnalysis.action(), commonAnalysis.confidence());
    }
}
