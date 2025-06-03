package org.ttrader.analysisUtil;

public enum AnalysisAction {
    BUY("BUY"), SELL("SELL"), HOLD("HOLD");
    public static AnalysisAction[] all = {
        BUY, SELL, HOLD
    };
    private final String value;
    AnalysisAction(String value) {
        this.value = value;
    }
    public String getValue() { return value; }
    public static AnalysisAction fromString(String value) {
        for (AnalysisAction analysisAction : all) {
            if (analysisAction.value.equals(value)) {
                return analysisAction;
            }
        }
        return null;
    }
}
