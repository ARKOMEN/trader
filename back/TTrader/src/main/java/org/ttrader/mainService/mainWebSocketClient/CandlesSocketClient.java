package org.ttrader.mainService.mainWebSocketClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.ttrader.mainService.DatabaseService;
import org.ttrader.mainService.entities.CandleEntity;
import org.ttrader.mainService.entities.CandleEntityFull;
import org.ttrader.mainService.entities.CandleFunny;
import org.ttrader.util.TickerPrice;
import org.ttrader.util.CandlePeriod;

import java.util.*;
import java.util.stream.Collectors;

public class CandlesSocketClient extends TextWebSocketHandler {

    private Set<String> tickers;

    private final DatabaseService databaseService;
    private final Map<String, List<? extends CandleEntityFull>> candles = new HashMap<>();
    public CandlesSocketClient(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String text = message.getPayload();

        JsonNode node;
        try {
            node = new ObjectMapper().readTree(text);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String type = node.get("type").asText();
        switch (type) {
            case "tickers" -> {
                Set<String> newTickers = new HashSet<>();
                for (JsonNode ticker : node.get("tickers")) {
                    newTickers.add(ticker.asText());
                }
                databaseService.informAboutTickers(newTickers);
            }
            case "prices" -> {
                List<TickerPrice> tickerPrices = new ArrayList<>();
                for (JsonNode candle : node.get("prices")) {
                    tickerPrices.add(new TickerPrice(
                        candle.get("i").asText(),
                        candle.get("p").asDouble(),
                        candle.get("t").asLong()
                    ));
                }

                List<CandleEntityFull> newCandles = new ArrayList<>();

                for (TickerPrice tickerPrice : tickerPrices) {

                    List<CandleEntityFull> candleEntityList = (List<CandleEntityFull>) candles.computeIfAbsent(tickerPrice.ticker(), ignored ->
                        {
                            List<CandleEntityFull> temp = Arrays.stream(CandlePeriod.all).map(
                                p -> new CandleFunny(tickerPrice, p)
                            ).collect(Collectors.toList());
                            newCandles.addAll(temp);
                            return temp;
                        }
                    );

                    for (int i = 0; i < candleEntityList.size(); ++i) {
                        CandleEntityFull candle = candleEntityList.get(i);
                        long normalizedTimestamp = CandlePeriod.normalize(tickerPrice.timestamp(), candle.getPeriod());
                        if (candle.getTimestamp() == normalizedTimestamp) {

                            candle.setClose(tickerPrice.price());
                            if (tickerPrice.price() > candle.getHigh())
                                candle.setHigh(tickerPrice.price());
                            if (tickerPrice.price() < candle.getLow())
                                candle.setLow(tickerPrice.price());
                        } else {
                            newCandles.add(candle);
                            CandleEntityFull c = new CandleFunny(tickerPrice, candle.getPeriod());
                            candleEntityList.set(i, c);
                        }
                    }
                }
                databaseService.saveAllFunnies(newCandles);
            }
        }
    }
}
