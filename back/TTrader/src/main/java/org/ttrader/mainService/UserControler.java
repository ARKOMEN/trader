package org.ttrader.mainService;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.ttrader.mainService.entities.CandleEntity;
import org.ttrader.mainService.entities.CandleEntityShort;
import org.ttrader.util.CandlePeriod;

import javax.json.Json;
import javax.json.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.ttrader.secondaryServicesUtil.SecondaryWebsocketClient.ofObjects;

@RestController
@RequestMapping("/")
@Profile("main-service")
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
    public String recomend() {
        return Json.createObjectBuilder().add("recomend", ofObjects(
            userService.getTickers().stream().map(
                ticker -> Json.createObjectBuilder()
                    .add("ticker", ticker)
                    .add("action", Math.random() < 0.3 ? "SALE" : Math.random() < 0.5 ? "BUY" : "HOLD")
                    .add("reability", Math.random())
                    .add("company", "COMPANY " + ticker)
                    .build()
            ).toList()
        )).build().toString();
    }

//    @GetMapping("/news")
//    public JsonObject news() {
//
//    }

    @GetMapping("/ticker/{ticker}")
    public String ticker(@PathVariable String ticker, @RequestParam Long unit, @RequestParam Long count) {

        CandlePeriod candlePeriod;
        {
            Optional<CandlePeriod> period1 = CandlePeriod.ofPeriod(unit);
            if (period1.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid time unit");
            }
            candlePeriod = period1.get();
        }
        if (count > candlePeriod.getLiveTime()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Period is too long");
        }

        List<CandleEntityShort> history = userService.getHistory(ticker, unit, count);

        return Json.createObjectBuilder()
            .add("company", "COMPANY " + ticker)
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
    public String getCurrent(@PathVariable String ticker, @RequestParam Long unit) {
        CandlePeriod candlePeriod;
        {
            Optional<CandlePeriod> period1 = CandlePeriod.ofPeriod(unit);
            if (period1.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid time unit");
            }
            candlePeriod = period1.get();
        }

        Optional<CandleEntity> candleEntity = userService.getCurrent(ticker, unit);

        if (candleEntity.isEmpty())
            return Json.createObjectBuilder().add("present", false).build().toString();

        CandleEntity candle = candleEntity.get();

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
    public String getPeriods() {
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
