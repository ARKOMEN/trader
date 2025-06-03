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
        websocketClient.informAboutTickers(tickers);
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
            System.err.println("running!");
            for (String ticker : tickers) {
                this.subscribe(ticker);
            }
        };
    }

    // Когда соединение установлено
    @OnOpen
    public void onOpen(Session session) {
        System.err.println("✅ Connected to Finnhub WebSocket!");
        this.session = session;
    }


    // Обработка входящих сообщений
    @OnMessage
    public void onMessage(String message) {
        System.err.println("📩 Message from server: " + message);
        try{
            JsonNode node = new ObjectMapper().readTree(message);
            if (!"trade".equals(node.get("type").asText())) {
                return;
            }
            List<TickerPrice> newCandles = new ArrayList<>();
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
            websocketClient.sendMessage(newCandles);
        }
        catch (Exception e) {
            e.printStackTrace();// todo logging errors
        }
    }

    public void subscribe(String symbol) {
        if (session != null && session.isOpen()) {
            try {
                String message = objectMapper.writeValueAsString(
                    new SubscriptionMessage("subscribe", symbol)
                );
                session.getAsyncRemote().sendText(message);
                //subscribedSymbols.add(symbol);
                System.err.println("🔔 Subscribed to: " + symbol);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void unsubscribe(String symbol) {
        if (session != null && session.isOpen()) {
            try {
                String message = objectMapper.writeValueAsString(
                    new SubscriptionMessage("unsubscribe", symbol)
                );
                session.getAsyncRemote().sendText(message);
                //subscribedSymbols.remove(symbol);
                System.err.println("🔕 Unsubscribed from: " + symbol);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Закрытие соединения
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.err.println("❌ Connection Closed: " + reason.getReasonPhrase());
    }

    // Обработка ошибок
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("❌ Error: " + throwable.getMessage());
    }

    // Вспомогательный класс для сообщений
    private record SubscriptionMessage(
        String type,
        String symbol
    ) {

    }
}
