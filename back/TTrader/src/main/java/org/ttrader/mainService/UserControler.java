package org.ttrader.mainService;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.ttrader.mainService.analisys.CompanyDescriptor;
import org.ttrader.mainService.entities.CandleEntity;
import org.ttrader.mainService.entities.CandleEntityFull;
import org.ttrader.mainService.entities.CandleEntityShort;
import org.ttrader.util.CandlePeriod;

import javax.json.Json;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.ttrader.util.TTraderUtil.ofObjects;

@RestController
@RequestMapping("/api")
@Profile("finnhub-service")
public class UserControler {

    private final UserService userService;

    public UserControler(UserService userService) {
        System.err.println("I AM ALIVE");
        this.userService = userService;
    }

    @GetMapping("/hello")
    public String hello() {
        return Json.createObjectBuilder().add("Hello", "world").build().toString();
    }

    @GetMapping("/recomend")
    public String recomend(@RequestHeader(value = "X-Correlation-Id", required = false) String tempCorr) {

        String correlationId = tempCorr == null ? UUID.randomUUID().toString() : tempCorr;

        System.err.println(correlationId + " : [controller] GET /reccomend");

        System.err.println(correlationId + " : [controller] retrieving recommendations...");
        return Json.createObjectBuilder().add("recommend", ofObjects(
            userService.getRecomendations(tempCorr).stream().map(
                recomendation -> {
                    CompanyDescriptor companyDescriptor = userService.getCompany(correlationId, recomendation.ticker());
                    return Json.createObjectBuilder()
                        .add("ticker", recomendation.ticker())
                        .add("action", recomendation.analysisAction().getValue())
                        .add("reliability", recomendation.confidence())
                        .add("company", companyDescriptor.companyName())
                        .add("url", companyDescriptor.url())
                        .build();
                }
            ).toList()
        )).build().toString();
//        return Json.createObjectBuilder().add("recomend", ofObjects(
//            userService.getTickers().stream().map(
//                ticker -> Json.createObjectBuilder()
//                    .add("ticker", ticker)
//                    .add("action", Math.random() < 0.3 ? "SALE" : Math.random() < 0.5 ? "BUY" : "HOLD")
//                    .add("reability", Math.random())
//                    .add("company", "COMPANY " + ticker)
//                    .build()
//            ).toList()
//        )).build().toString();
    }

    @GetMapping("/news")
    public String news(@RequestHeader(value = "X-Correlation-Id", required = false) String tempCorr) {

        String correlationId = tempCorr == null ? UUID.randomUUID().toString() : tempCorr;

        System.err.println(correlationId + " : [controller] GET /news");

        System.err.println(correlationId + " : [controller] retrieving news...");
        return Json.createObjectBuilder().add("news", ofObjects(
            userService.getNews(correlationId).stream().map(
                newsShort -> Json.createObjectBuilder()
                    .add("ticker", newsShort.getTicker().getTicker())
                    .add("title", newsShort.getTitle())
                    .add("description", newsShort.getDescription())
                    .add("url", newsShort.getUrl())
                    .build()
            ).toList()
        )).build().toString();
    }

    @GetMapping("/ticker/{ticker}")
    public String ticker(@PathVariable String ticker, @RequestParam Long unit, @RequestParam Long count,
                         @RequestHeader(value = "X-Correlation-Id", required = false) String tempCorr) {

        String correlationId = tempCorr == null ? UUID.randomUUID().toString() : tempCorr;

        System.err.println(correlationId + " : [controller] GET /reccomend/" + ticker + " unit = " + unit + ", count = " + count);

        CandlePeriod candlePeriod;
        {
            Optional<CandlePeriod> period1 = CandlePeriod.ofPeriod(unit);
            if (period1.isEmpty()) {
                System.err.println(correlationId + " : [controller] bad request (unit not exist)");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid time unit");
            }
            candlePeriod = period1.get();
        }
        if (count > candlePeriod.getLiveTime()) {
            System.err.println(correlationId + " : [controller] bad request (period is too long)");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Period is too long");
        }

        System.err.println(correlationId + " : [controller] retrieving history info...");

        List<CandleEntityShort> history = userService.getHistory(correlationId, ticker, unit, count);

        CompanyDescriptor companyDescriptor = userService.getCompany(correlationId, ticker);

        return Json.createObjectBuilder()
            .add("company", companyDescriptor.companyName())
            .add("url", companyDescriptor.url())
            .add("candles",
                ofObjects(history.stream().map(
                    candle -> Json.createObjectBuilder()
                        .add("t", candle.getTimestamp())
                        .add("o", candle.getOpen())
                        .add("c", candle.getClose())
                        .add("h", candle.getHigh())
                        .add("l", candle.getLow())
                        .build()
                ).toList())
            ).build().toString();
    }

    @GetMapping("/ticker/{ticker}/current")
    public String getCurrent(@PathVariable String ticker, @RequestParam Long unit,
                             @RequestHeader(value = "X-Correlation-Id", required = false) String tempCorr) {

        String correlationId = tempCorr == null ? UUID.randomUUID().toString() : tempCorr;

        System.err.println(correlationId + " : [controller] GET /ticker/" + ticker + "/current unit = " + unit);

        CandlePeriod candlePeriod;
        {
            Optional<CandlePeriod> period1 = CandlePeriod.ofPeriod(unit);
            if (period1.isEmpty()) {
                System.err.println(correlationId + " : [controller] bad request (unit not exist)");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid time unit");
            }
            candlePeriod = period1.get();
        }

        System.err.println(correlationId + " : [controller] retrieving current price...");
        Optional<CandleEntityFull> candleEntity = userService.getCurrent(correlationId, ticker, unit);

        if (candleEntity.isEmpty())
            return Json.createObjectBuilder().add("present", false).build().toString();

        CandleEntityFull candle = candleEntity.get();

        return Json.createObjectBuilder()
            .add("present", true)
            .add("current", Json.createObjectBuilder()
                .add("t", candle.getTimestamp())
                .add("o", candle.getOpen())
                .add("c", candle.getClose())
                .add("h", candle.getHigh())
                .add("l", candle.getLow())
                .build()
            ).build().toString();
    }

    @GetMapping("/units")
    public String getPeriods(@RequestHeader(value = "X-Correlation-Id", required = false) String tempCorr) {

        String correlationId = tempCorr == null ? UUID.randomUUID().toString() : tempCorr;

        System.err.println(correlationId + " : [controller] GET /units");
        System.err.println(correlationId + " : [controller] retrieving units info...");

        return Json.createObjectBuilder()
            .add("periods", ofObjects(
                Arrays.stream(CandlePeriod.all).map(
                    p -> Json.createObjectBuilder()
                        .add("secs", p.getUnixPeriod())
                        .add("enLong", p.getEnName())
                        .add("short", p.getEnShortName())
                        .add("ruLong", p.getRuName())
                        .add("maxPeriod", p.getLiveTime())
                        .build()
                ).toList()
            )).build().toString();
    }

//    @GetMapping("/compare/{ticker1}/{ticker2}")
//    public JsonObject compare(@PathVariable String ticker1, @PathVariable String ticker2) {
//        //...
//    }
}
