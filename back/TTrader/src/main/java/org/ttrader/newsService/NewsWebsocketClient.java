package org.ttrader.newsService;

import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    private final RabbitTemplate rabbitTemplate;

    public NewsWebsocketClient(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public synchronized void sendMessage(List<NewsDescriptor> news) {
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
        rabbitTemplate.convertAndSend("news", message);
    }
}
