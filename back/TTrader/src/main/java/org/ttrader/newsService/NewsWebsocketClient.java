package org.ttrader.newsService;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.ttrader.newsUtil.NewsDescriptor;

import javax.json.Json;
import java.util.List;

import static org.ttrader.util.TTraderUtil.ofObjects;


@Service
@Profile("news-service")
public class NewsWebsocketClient {

    private WebSocketClient client;
    private final String WS_URI = "ws://localhost:8080/news";

    private WebSocketSession session;

    @PostConstruct
    public synchronized void start() {
        System.err.println("starting...");
        try {
            for (int i = 0; true; ++i) {
                try {
                    client = new StandardWebSocketClient();
                    session = client.execute(new TextWebSocketHandler(), WS_URI).get();
                    break;
                }
                catch (Exception e) {
                    if (i < 5) {
                        System.err.println("Connection failure! Retrying reconnection in 1 second...");
                        Thread.sleep(1000);
                    }
                    else throw new Exception("Can't connect to server", e);
                }
            }

            System.out.println("WebSocket connection established to " + WS_URI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            this.notifyAll();
        }
    }

    public synchronized void sendMessage(List<NewsDescriptor> news) {
        if (session != null && session.isOpen()) {
            try {
                String message = Json.createObjectBuilder()
                    .add("news",
                        ofObjects(news.stream().map(e -> Json.createObjectBuilder()
                            .add("k", e.ticker())
                            .add("t", e.title())
                            .add("d", e.description())
                            .add("i", e.id())
                            .add("u", e.url())
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
