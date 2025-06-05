package org.ttrader.finnhubService;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.websocket.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.ttrader.util.TickerPrice;
import org.ttrader.secondaryServicesUtil.SecondaryWebsocketClient;
import org.ttrader.util.CandlePeriod;

import java.net.URI;
import java.util.*;

@Service
@ClientEndpoint
@Profile("finnhub-service")
public class FinnhubService {

    @Value("${ttrader.finnhub.api_key}")
    private String token;

    private final SecondaryWebsocketClient websocketClient;

    //private final List<String> subscribedSymbols = new CopyOnWriteArrayList<>();

    private static final String WS_URL = "wss://ws.finnhub.io?token=";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Session session;


    private static final String[] tickers = new String[] {
        "AAPL", "GOOGL", "NVDA", "MSFT", "AMZN", "META", "TSLA", "NFLX", "DIS", "INTC"
    };

    public FinnhubService(SecondaryWebsocketClient websocketClient) {
        this.websocketClient = websocketClient;
    }

    // Автоматическое подключение при старте приложения
    @PostConstruct
    public void init() {
        String event = UUID.randomUUID().toString();
        System.err.println(event + " : [finnhub service] informing about tickers: " + tickers.length);
        websocketClient.informAboutTickers(event, tickers);
        connect();
    }

    @PreDestroy
    public void finish() {
        for (String ticker : tickers) {
            this.unsubscribe(ticker);
        }
    }

    // Подключение к WebSocket
    private void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, URI.create(WS_URL + token));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Bean
    public CommandLineRunner CommandLineRunnerBean() {
        return (args) -> {
            for (String ticker : tickers) {
                this.subscribe(ticker);
            }
        };
    }

    // Когда соединение установлено
    @OnOpen
    public void onOpen(Session session) {
        String event = UUID.randomUUID().toString();
        System.err.println(event + " : [finnhub service] connected");
        this.session = session;
    }


    // Обработка входящих сообщений
    @OnMessage
    public void onMessage(String message) {
        String event = UUID.randomUUID().toString();
        System.err.println(event + " : [finnhub service] got message");
        try{
            JsonNode node = new ObjectMapper().readTree(message);
            if (!"trade".equals(node.get("type").asText())) {
                return;
            }
            List<TickerPrice> newCandles = new ArrayList<>();
            System.err.println(event + " : [finnhub service] got info about tickers: " + node.get("data").size());
            for (JsonNode node1 : node.get("data")) {
                try {
                    String ticker = node1.get("s").asText();
                    long secondsTimestamp = node1.get("t").asLong()/1000;
                    double price = node1.get("p").asDouble();
                    newCandles.add(new TickerPrice(ticker, price, secondsTimestamp));
                }
                catch (Exception e) {
                    System.err.println("uh-oh!");
                    System.err.println(node1.asText());
                    e.printStackTrace();
                }
            }
            websocketClient.sendMessage(event, newCandles);
        }
        catch (Exception e) {
            e.printStackTrace();// todo logging errors
        }
    }

    public void subscribe(String symbol) {
        String event = UUID.randomUUID().toString();
        if (session != null && session.isOpen()) {
            try {
                String message = objectMapper.writeValueAsString(
                    new SubscriptionMessage("subscribe", symbol)
                );
                session.getAsyncRemote().sendText(message);
                //subscribedSymbols.add(symbol);
                System.err.println(event + " : [finnhub service] subscribed to: " + symbol);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void unsubscribe(String symbol) {
        String event = UUID.randomUUID().toString();
        if (session != null && session.isOpen()) {
            try {
                String message = objectMapper.writeValueAsString(
                    new SubscriptionMessage("unsubscribe", symbol)
                );
                session.getAsyncRemote().sendText(message);
                //subscribedSymbols.remove(symbol);
                System.err.println("🔕 Unsubscribed from: " + symbol);
                System.err.println(event + " : [finnhub service] unsubscribed from: " + symbol);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Закрытие соединения
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        String event = UUID.randomUUID().toString();
        System.err.println(event + " : [finnhub service] connection closed: " + reason.getReasonPhrase());
    }

    // Обработка ошибок
    @OnError
    public void onError(Session session, Throwable throwable) {
        String event = UUID.randomUUID().toString();
        System.err.println(event + " : [finnhub service] error: " + throwable.getMessage());
    }

    // Вспомогательный класс для сообщений
    private record SubscriptionMessage(
        String type,
        String symbol
    ) {

    }
}
