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
        this.databaseService = databaseService;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new MainSocketClient(databaseService), "/candles")/*.setAllowedOrigins("https://localhost")*/;
    }

}
