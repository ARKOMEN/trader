package org.ttrader.mainService;

import org.springframework.stereotype.Service;

@Service
public class TickerService {
    //actually, the most useful class in the project (im almost not joking)
    private static final String[] tickers = new String[] {
        "AAPL", "GOOGL", "NVDA", "MSFT", "AMZN", "META", "TSLA", "NFLX", "DIS", "INTC"
    };
    public String[] getTickers() { return tickers; }
}
