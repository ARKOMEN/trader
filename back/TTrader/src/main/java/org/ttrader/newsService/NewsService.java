package org.ttrader.newsService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.websocket.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.ttrader.newsUtil.NewsDescriptor;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@EnableScheduling
@ClientEndpoint
@Profile("news-service-wrong")
public class NewsService {


    private final NewsWebsocketClient websocketClient;

    private String WS_URL = "https://newsapi.org";
    @Value("${ttrader.newsapi.api_key}")
    private String token;
    private WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Session session;


    public NewsService(NewsWebsocketClient websocketClient) {
        this.websocketClient = websocketClient;
    }

    @PostConstruct
    public void start() {
        webClient = WebClient.create(WS_URL);
    }

    // Автоматическое подключение при старте приложения
    // Подключение к WebSocket
    private void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, URI.create(WS_URL + token));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Обработка входящих сообщений
    @Scheduled(fixedDelay = 1000*60/* *60*/)//1 hour
    public void checkNews() {
        try{
            String message = webClient.get()
                .uri("/v2/top-headlines")
                .attribute("apiKey", token)
                .retrieve()
                .bodyToMono(String.class)
                .block();

            System.err.println("📩 Message from server: " + message);

            JsonNode node = new ObjectMapper().readTree(message);
            if (!node.has("articles")) {
                return;
            }

            List<NewsDescriptor> newNews = new ArrayList<>();
            for (JsonNode node1 : node.get("articles")) {
                try {

                    String title = node1.get("title").asText();
                    String description = node1.get("description").asText();
                    String content = node1.get("content").asText();
                    String url = node1.get("url").asText();
                    long id = node1.get("id").asLong();

                    newNews.add(new NewsDescriptor(title, description, content, url, id));
                }
                catch (Exception e) {
                    System.err.println("uh-oh!");
                    System.err.println(node1.asText());
                    e.printStackTrace();
                }
            }
            websocketClient.sendMessage(newNews);
        }
        catch (Exception e) {
            e.printStackTrace();// todo logging errors
        }
    }
}
