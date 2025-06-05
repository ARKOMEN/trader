package org.ttrader.secondaryServicesUtil;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.ttrader.util.TickerPrice;

import javax.json.Json;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.ttrader.util.TTraderUtil.ofObjects;
import static org.ttrader.util.TTraderUtil.ofStrings;


@Service
@Conditional(SecondaryWebsocketServiceCondition.class)
public class SecondaryWebsocketClient {
    private final RabbitTemplate rabbitTemplate;

    public SecondaryWebsocketClient(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public synchronized void informAboutTickers(String correlationId, String[] tickers) {
        System.err.println(correlationId + " : [secondary service] inform about tickers (array): " + tickers.length);
        rabbitTemplate.convertAndSend(
            "stocks",
            Json.createObjectBuilder()
                .add("type", "tickers")
                .add("event", correlationId)
                .add("tickers", ofStrings(tickers))
                .build().toString()
        );
    }

    public synchronized void informAboutTickers(String correlationId, Collection<String> tickers) {
        System.err.println(correlationId + " : [secondary service] inform about tickers (collection): " + tickers.size());
        rabbitTemplate.convertAndSend(
            "stocks",
            Json.createObjectBuilder()
                .add("type", "tickers")
                .add("event", correlationId)
                .add("tickers", ofStrings(tickers))
                .build().toString()
        );
    }

    public synchronized void sendMessage(String correlationId, List<TickerPrice> candles) {
        System.err.println(correlationId + " : [secondary service] send candles: " + candles.size());
        rabbitTemplate.convertAndSend(
            "stocks",
            Json.createObjectBuilder()
                .add("type", "prices")
                .add("event", correlationId)
                .add("prices",
                    ofObjects(candles.stream().map(e -> Json.createObjectBuilder()
                        .add("i", e.ticker())
                        .add("t", e.timestamp())
                        .add("p", e.price())
                        .build()
                    ).toList())
                ).build().toString()
        );
    }
}
