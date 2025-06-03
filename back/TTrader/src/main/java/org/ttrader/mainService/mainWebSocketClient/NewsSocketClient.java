package org.ttrader.mainService.mainWebSocketClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.ttrader.mainService.DatabaseService;
import org.ttrader.mainService.entities.CandleEntity;
import org.ttrader.newsUtil.NewsDescriptor;
import org.ttrader.util.CandlePeriod;
import org.ttrader.util.TickerPrice;

import java.util.*;
import java.util.stream.Collectors;

public class NewsSocketClient extends TextWebSocketHandler {

    private Set<String> tickers;

    private final DatabaseService databaseService;
    private final Map<String, List<CandleEntity>> candles = new HashMap<>();
    public NewsSocketClient(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        String text = message.getPayload();

        JsonNode node = null;
        try {
            node = new ObjectMapper().readTree(text);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        List<NewsDescriptor> newsDescriptors = new ArrayList<>();
        for (JsonNode candle : node.get("news")) {
            newsDescriptors.add(new NewsDescriptor(
                candle.get("k").asText(),
                candle.get("t").asText(),
                candle.get("d").asText(),
                candle.get("u").asText(),
                candle.get("i").asLong()
            ));
        }

        System.err.println("got news!");

        //...

    }
}
