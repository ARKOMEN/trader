package org.ttrader.mainService.mainClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.ClientEndpoint;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.ttrader.analysisUtil.AnalysisAction;
import org.ttrader.analysisUtil.CommonAnalysis;
import org.ttrader.mainService.entities.CandleEntityShort;

import java.util.List;

@Service
@ClientEndpoint
@Profile("main-service")
public class AnalysisClient {

    private static final String WS_URL = "http://localhost:8040/api";
    private final WebClient webClient = WebClient.create(WS_URL);

    public CommonAnalysis analyse(List<CandleEntityShort> candles) {
        String result = webClient.post().uri(
            uriBuilder -> uriBuilder.path("/analyse").build()
        ).body("body", String.class)
            .retrieve()
            .bodyToMono(String.class)
            .block();
        JsonNode node;
        try {
            node = new ObjectMapper().readTree(result);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new CommonAnalysis(
            AnalysisAction.valueOf(node.get("action").asText()),
            node.get("confidence").asInt()
        );

    }
}
