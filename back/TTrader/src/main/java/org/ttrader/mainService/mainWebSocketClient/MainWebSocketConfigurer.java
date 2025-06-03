package org.ttrader.mainService.mainWebSocketClient;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.ttrader.mainService.DatabaseService;

@Profile("main-service")
@Configuration
@EnableWebSocket
public class MainWebSocketConfigurer implements WebSocketConfigurer {

    private final DatabaseService databaseService;

    public MainWebSocketConfigurer(DatabaseService databaseService) {
        System.err.println("?????????????????????????????????????im here guys????????????????????");
        this.databaseService = databaseService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!im here guys!!!!!!!!!!!!!!!!!!!!");
        registry.addHandler(new CandlesSocketClient(databaseService), "/candles")/*.setAllowedOrigins("https://localhost")*/;
        registry.addHandler(new NewsSocketClient(databaseService), "/news");
    }

}
