package org.ttrader.newsService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.ClientEndpoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.ttrader.newsUtil.NewsDescriptor;
import org.ttrader.util.TTraderUtil;

import java.util.*;
import java.util.function.Function;

@Service
@EnableScheduling
@ClientEndpoint
@Profile("news-service")
public class NewsService {

    @Value("${ttrader.finnhub.api_key}")
    private String token;

    private final NewsWebsocketClient websocketClient;

    //private final List<String> subscribedSymbols = new CopyOnWriteArrayList<>();

    private static final String WS_URL = "https://finnhub.io/api/v1";
    private final WebClient webClient = WebClient.create(WS_URL);

    private static final String[] tickers = new String[] {
        "AAPL", "GOOGL", "NVDA", "MSFT", "AMZN", "META", "TSLA", "NFLX", "DIS", "INTC"
    };

    public NewsService(NewsWebsocketClient websocketClient) {
        this.websocketClient = websocketClient;
    }

    // Обработка входящих сообщений
    @Scheduled(fixedDelay = 1000*60/* *60*/)//1 hour
    public void checkNews() {
        System.err.println("checking...");
        try{
            List<NewsDescriptor> messages = Arrays.stream(tickers).map(
                (Function<String, List<NewsDescriptor>>) ticker -> {
                    String message = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                            .path("/company-news")
                            .queryParam("token", token)
                            .queryParam("symbol", ticker)
                            .queryParam("from", TTraderUtil.getStringDate(-1))
                            .queryParam("to", TTraderUtil.getStringDate(1))
                            .build()
                        )
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
                    //System.err.println("📩 Message from server: " + message);

                    JsonNode node;
                    try {
                        node = new ObjectMapper().readTree(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return List.of();
                    }
                    if (!node.isArray()) {
                        return List.of();
                    }

                    List<NewsDescriptor> newNews = new ArrayList<>();
                    for (JsonNode node1 : node) {
                        try {

                            String title = node1.get("headline").asText();
                            String description = node1.get("summary").asText();
                            String url = node1.get("url").asText();
                            long id = node1.get("id").asLong();
                            long datetime = node1.get("datetime").asLong();

                            newNews.add(new NewsDescriptor(ticker, title, description, url, id, datetime));
                        } catch (Exception e) {
                            System.err.println("uh-oh!");
                            System.err.println(node1.asText());
                            e.printStackTrace();
                        }
                    }
                    return newNews;
                }
            ).flatMap(Collection::stream).toList();

            websocketClient.sendMessage(messages);
        }
        catch (Exception e) {
            e.printStackTrace();// todo logging errors
        }
    }
}
