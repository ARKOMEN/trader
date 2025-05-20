package org.ttrader.mainService;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ttrader.util.TickerPrice;
import org.ttrader.secondaryServicesUtil.SecondaryWebsocketClient;
import org.ttrader.util.CandlePeriod;

import java.util.*;

@Component
@EnableScheduling
@Profile("main-service")
public class TheGreatCrutch {

    private final DatabaseService databaseService;
    private final SecondaryWebsocketClient websocketClient;

    private static final String[] tickers = new String[] {
        "AAPL", "GOOGL", "NVDA", "MSFT", "AMZN", "META", "TSLA", "NFLX", "DIS", "INTC"
    };

    private static final double[] prices = Arrays.stream(tickers).mapToDouble(p -> Math.random()*100.).toArray();

    public TheGreatCrutch(DatabaseService databaseService, SecondaryWebsocketClient websocketClient) {
        this.databaseService = databaseService;
        this.websocketClient = websocketClient;
    }

    @Scheduled(fixedRate = 1000)
    public void crunchThings() {
        long secondsTimestamp = System.currentTimeMillis() / 1000L;

        List<TickerPrice> tickerPrices = new ArrayList<>();

        for (int j = 0; j < tickers.length; ++j) {
            String ticker = tickers[j];
            double price = (prices[j] = Math.max(Math.min(prices[j] + Math.random()*10. - 5., 200.), 20.));
            tickerPrices.add(new TickerPrice(ticker, price, secondsTimestamp));
        }
        websocketClient.sendMessage(tickerPrices);
    }
}
