package org.ttrader.mainService.mainClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.ttrader.mainService.DatabaseService;
import org.ttrader.mainService.entities.CandleEntity;
import org.ttrader.newsUtil.NewsDescriptor;

import java.util.*;

@Service
public class NewsSocketClient {

    private Set<String> tickers;

    private final DatabaseService databaseService;
    private final Map<String, List<CandleEntity>> candles = new HashMap<>();
    public NewsSocketClient(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @RabbitListener(queues = "news")
    public void handleTextMessage(String text) {
        System.err.println("news alive =)");

        JsonNode node;
        try {
            node = new ObjectMapper().readTree(text);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        List<NewsDescriptor> newsDescriptors = new ArrayList<>();
        System.err.println("got news: ");
        for (JsonNode candle : node.get("news")) {
            newsDescriptors.add(new NewsDescriptor(
                candle.get("k").asText(),
                candle.get("t").asText(),
                candle.get("d").asText(),
                candle.get("u").asText(),
                candle.get("i").asLong(),
                candle.get("m").asLong()
            ));
        }

        databaseService.saveNews(newsDescriptors);

    }
}
