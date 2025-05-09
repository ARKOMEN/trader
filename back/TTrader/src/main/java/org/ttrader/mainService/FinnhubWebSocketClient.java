package org.ttrader.mainService;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import jakarta.websocket.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.ttrader.mainService.entities.CandleEntity;
import org.ttrader.util.CandlePeriod;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@ClientEndpoint
public class FinnhubWebSocketClient {

    @Value("${finnhub.api_key}")
    private String token;

    @Autowired
    private TickerService tickerService;

    @Autowired
    private DatabaseService databaseService;

    private static final String WS_URL = "wss://ws.finnhub.io?token=";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Session session;
    private Map<String, List<CandleEntity>> candles;


    @PostConstruct
    public void init() {
        //List<CandleEntity> temp = Arrays.stream(CandlePeriod.all).map(p -> new CandleEntity("---", ));
//        candles = Arrays.stream(tickerService.getTickers()).map(
//            t -> Arrays.stream(CandlePeriod.all).map(p -> new CandleEntity(null, t, 0, 0, 1000000, ))
//        );
        connect();
    }

    // Подключение к WebSocket
    private void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, URI.create(WS_URL + token));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Когда соединение установлено
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("✅ Connected to Finnhub WebSocket!");
        for (String ticker : tickerService.getTickers()) {
            this.subscribe(ticker);
        }
    }

    // Обработка входящих сообщений
    @OnMessage
    public void onMessage(String message) {
        System.out.println("📩 Message from server: " + message);
        try{
            JsonNode node = new ObjectMapper().readTree(message);
            if (!"trade".equals(node.get("type").asText())) {
                return;
            }
            for (JsonNode node1 : node.get("data")) {
                try {
                    String ticker = node1.get("s").asText();
                    long secondsTimestamp = node1.get("t").asLong()/1000;
                    double price = node1.get("p").asDouble();

                    List<CandleEntity> candleEntityList = candles.computeIfAbsent(ticker, ignored ->
                        {
                            List<CandleEntity> temp = Arrays.stream(CandlePeriod.all).map(
                                p -> new CandleEntity(
                                    ticker,
                                    price, price, price, price,
                                    p.normalize(secondsTimestamp), p.getUnixPeriod())
                            ).toList();
                            databaseService.saveAll(temp);
                            return temp;
                        }
                    );

                    for (int i = 0; i < candleEntityList.size(); ++i) {
                        CandleEntity candle = candleEntityList.get(i);
                        long normalizedTimestamp = CandlePeriod.normalize(secondsTimestamp, candle.getPeriod());
                        if (candle.getTimestamp() == normalizedTimestamp) {
                            candle.setClose(price);
                            if (price > candle.getHigh())
                                candle.setHigh(price);
                            if (price < candle.getLow())
                                candle.setLow(price);
                        }
                        else {
                            databaseService.saveCandle(candle);
                            candleEntityList.set(i, new CandleEntity(ticker, price, normalizedTimestamp, candle.getPeriod()));
                        }
                    }
                }
                catch (Exception e) {
                    System.err.println("uh-oh!");
                    System.err.println(node1.asText());
                }

            }
        }
        catch (Exception e) {
            e.printStackTrace();// todo logging errors
        }
    }

    // Подписка на тикер
    public void subscribe(String symbol) {
        if (session != null && session.isOpen()) {
            try {
                String message = objectMapper.writeValueAsString(
                    new SubscriptionMessage("subscribe", symbol)
                );
                session.getAsyncRemote().sendText(message);
                System.out.println("🔔 Subscribed to: " + symbol);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Отписка от тикера
    public void unsubscribe(String symbol) {
        if (session != null && session.isOpen()) {
            try {
                String message = objectMapper.writeValueAsString(
                    new SubscriptionMessage("unsubscribe", symbol)
                );
                session.getAsyncRemote().sendText(message);
                System.out.println("🔕 Unsubscribed from: " + symbol);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Закрытие соединения
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("❌ Connection Closed: " + reason.getReasonPhrase());
    }

    // Обработка ошибок
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("❌ Error: " + throwable.getMessage());
    }

    // Вспомогательный класс для сообщений
    private record SubscriptionMessage(
        String type,
        String symbol
    ) {

    }
}
