package org.ttrader.mainService.mainClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.ClientEndpoint;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.ttrader.analysisUtil.AnalysisAction;
import org.ttrader.analysisUtil.CommonAnalysis;
import org.ttrader.mainService.entities.CandleEntityShort;

import javax.json.Json;
import java.util.List;

import static org.ttrader.util.TTraderUtil.ofObjects;

@Service
@ClientEndpoint
@Profile("main-service")
public class AnalysisClient {

    //private static final String WS_URL = "http://localhost:8040/api";
    //private final WebClient webClient = WebClient.create(WS_URL);

    private final RabbitTemplate rabbitTemplate;

    public AnalysisClient(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public CommonAnalysis analyse(List<CandleEntityShort> candles) {
//        String result = webClient.post().uri(
//            uriBuilder -> uriBuilder.path("/analyse").build()
//        ).body("body", String.class)
//            .retrieve()
//            .bodyToMono(String.class)
//            .block();

        String result = (String)rabbitTemplate.convertSendAndReceive(
            "analysis",
            Json.createObjectBuilder().add("candles",
                ofObjects(
                    candles.stream().map(candle -> Json.createObjectBuilder()
                        .add("o", candle.getOpen())
                        .add("h", candle.getHigh())
                        .add("l", candle.getLow())
                        .add("c", candle.getClose())
                        .add("t", candle.getTimestamp())
                        .build()
                    ).toList()
                )).build().toString()
        );

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
