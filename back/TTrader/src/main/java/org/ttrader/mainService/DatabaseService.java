package org.ttrader.mainService;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.ttrader.mainService.entities.*;
import org.ttrader.mainService.repositories.CandleRepository;
import org.ttrader.mainService.repositories.NewsRepository;
import org.ttrader.mainService.repositories.TickerRepository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Profile("main-service")
public class DatabaseService {
    private final CandleRepository candleRepository;
    private final TickerRepository tickerRepository;
    private final NewsRepository newsRepository;

    private final Map<String, TickerEntity> tickers = new HashMap<>();

    private final Map<String, Map<Long, CandleEntityFull>> cache = new HashMap<>();

    public static long getCurrentTime() {
        return Instant.now().getEpochSecond();
    }

    public DatabaseService(CandleRepository candleRepository, TickerRepository tickerRepository, NewsRepository newsRepository) {
        this.candleRepository = candleRepository;
        this.tickerRepository = tickerRepository;
        this.newsRepository = newsRepository;
    }

    public void saveCandle(CandleEntity candle) { candleRepository.save(candle); }

    public void saveAllEntities(List<CandleEntity> candles) {
        System.err.println("Saving " + candles.size() + " candles");
        for (CandleEntity candle : candles) {
            candleRepository.save(candle);
            cache.get(candle.getTicker()).compute(candle.getPeriod(),
                (key, candle1) -> candle1 == null || candle.getTimestamp() > candle1.getTimestamp() ? candle : candle1
            );
        }
    }

    public void saveAllFunnies(List<? extends CandleEntityFull> candles) {
        System.err.println("Saving " + candles.size() + " candles");
        for (CandleEntityFull candle : candles) {
            candleRepository.save(candle.getOpen(), candle.getHigh(), candle.getLow(), candle.getClose(), candle.getTicker(), candle.getTimestamp(), candle.getPeriod());
            cache.get(candle.getTicker()).compute(candle.getPeriod(),
                (key, candle1) -> candle1 == null || candle.getTimestamp() > candle1.getTimestamp() ? candle : candle1
            );
        }
    }

    public List<CandleEntityShort> getHistory(String ticker, long unit, long amount) {
        return candleRepository.findByTickerAndPeriodAndTimestampGreaterThan(
            tickers.get(ticker),
            unit,
            getCurrentTime() - unit * (amount + 2));
    }

    public long clearByTimestampAndPeriod(long timestamp, long period) {
        return candleRepository.deleteByTimestampLessThanAndPeriodLessThanEqual(timestamp, period);
    }

    public void informAboutTickers(Collection<String> tickers) {
        Map<String, TickerEntity> newTickers = tickers.stream().filter(ticker -> !tickers.contains(ticker)).collect(
            Collectors.toMap(ticker -> ticker, TickerEntity::new, (ticker1, ticker2) -> ticker1)
        );
        this.tickers.putAll(newTickers);
        newTickers.forEach((t, ticker) -> {
            cache.put(t, new HashMap<>());
            tickerRepository.save(ticker);
        });
    }
    public Set<String> getTickers() { return tickers.keySet(); }
    public Optional<CandleEntityFull> getCurrent(String ticker, long unit) {
        return Optional.ofNullable(cache.get(ticker).get(unit));
    }
}
