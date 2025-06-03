package org.ttrader.analysisService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.ttrader.analysisUtil.AnalysisAction;
import org.ttrader.analysisUtil.AnalysisCandle;
import org.ttrader.analysisUtil.CommonAnalysis;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.List;

@Profile("analysis-service")
@Service
//@RestController
//@RequestMapping("/api")
public class AnalysisController {
    private final RabbitTemplate rabbitTemplate;

    public AnalysisController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "analysis")
    public String analyse(/*@RequestBody*/ String requestBody) {
        JsonNode node;
        List<AnalysisCandle> candles;
        try {
            node = new ObjectMapper().readTree(requestBody);
            JsonNode node1 = node.get("candles");
            candles = new ArrayList<>();
            for (JsonNode node2 : node1) {
                candles.add(new AnalysisCandle(
                    node2.get("o").asDouble(),
                    node2.get("h").asDouble(),
                    node2.get("l").asDouble(),
                    node2.get("c").asDouble(),
                    node2.get("t").asLong()
                ));
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong format =(", e);
        }

        CommonAnalysis result = evaluate(candles);
        return Json.createObjectBuilder()
            .add("action", result.action().getValue())
            .add("confidence", result.confidence())
            .build().toString();
    }

    private static CommonAnalysis evaluate(List<AnalysisCandle> candles) {
        int longPeriod = candles.size() - 1;
        int shortPeriod = longPeriod/10;
        if (candles.size() < longPeriod + 1 || shortPeriod < 1) {
            return new CommonAnalysis(AnalysisAction.HOLD, 0);
        }

        double shortMA = movingAverage(candles, shortPeriod);
        double longMA = movingAverage(candles, longPeriod);

        double prevShortMA = movingAverage(candles.subList(0, candles.size() - 1), shortPeriod);
        double prevLongMA = movingAverage(candles.subList(0, candles.size() - 1), longPeriod);

        if (prevShortMA < prevLongMA && shortMA > longMA) {
            return new CommonAnalysis(AnalysisAction.BUY, computeConfidence(shortMA, longMA));
        } else if (prevShortMA > prevLongMA && shortMA < longMA) {
            return new CommonAnalysis(AnalysisAction.SELL, computeConfidence(shortMA, longMA));
        } else {
            return new CommonAnalysis(AnalysisAction.HOLD, 0);
        }
    }

    private static double movingAverage(List<AnalysisCandle> candles, int period) {
        return candles.subList(candles.size() - period, candles.size()).stream()
            .mapToDouble(AnalysisCandle::close)
            .average()
            .orElse(0);
    }

    private static int computeConfidence(double shortMA, double longMA) {
        double diff = Math.abs(shortMA - longMA);
        double avg = (shortMA + longMA) / 2;
        return (int) Math.min(100, (diff / avg) * 1000); // normalize
    }
}
