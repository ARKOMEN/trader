package org.ttrader.secondaryServicesUtil;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.ttrader.util.TickerPrice;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import java.util.List;

import static org.ttrader.util.JsonUtil.ofStrings;


@Service
//@Profile("finnhub-service,tinkoff-service")
public class SecondaryWebsocketClient {

    private WebSocketClient client;
    private final String WS_URI = "ws://localhost:8080/candles";

    private static final String[] tickers = new String[] {
        "AAPL", "GOOGL", "NVDA", "MSFT", "AMZN", "META", "TSLA", "NFLX", "DIS", "INTC"
    };

    private WebSocketSession session;

    public static JsonArrayBuilder ofObjects(List<JsonObject> strings) {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (JsonObject string : strings) {
            jsonArrayBuilder.add(string);
        }
        return jsonArrayBuilder;
    }

    public synchronized void start() {
        try {
            client = new StandardWebSocketClient();
            session = client.execute(new TextWebSocketHandler(), WS_URI).get();

            session.sendMessage(new TextMessage(Json.createObjectBuilder()
                .add("type", "tickers")
                .add("tickers", ofStrings(tickers))
                .build().toString()
            ));
            System.out.println("WebSocket connection established to " + WS_URI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    public CommandLineRunner CommandLineRunnerBean() {
        return (args) -> start();
    }

    public synchronized void sendMessage(List<TickerPrice> candles) {
        if (session != null && session.isOpen()) {
            try {
                String message = Json.createObjectBuilder()
                    .add("type", "prices")
                    .add("prices",
                        ofObjects(candles.stream().map(e -> Json.createObjectBuilder()
                            .add("i", e.ticker())
                            .add("t", e.timestamp())
                            .add("p", e.price())
                            .build()
                        ).toList())
                    ).build().toString();
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("WebSocket session is not open!");
        }
    }
}
