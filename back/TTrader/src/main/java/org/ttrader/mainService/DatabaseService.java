package org.ttrader.mainService;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.ttrader.mainService.entities.CandleEntityShort;
import org.ttrader.mainService.repositories.CandleRepository;
import org.ttrader.mainService.entities.CandleEntity;

import java.time.Instant;
import java.util.*;

@Service
@Profile("main-service")
public class DatabaseService {
    private final CandleRepository candleRepository;

    private final Set<String> tickers = new HashSet<>();

    private final Map<String, Map<Long, CandleEntity>> cache = new HashMap<>();

    public static long getCurrentTime() {
        return Instant.now().getEpochSecond();
    }

    public DatabaseService(CandleRepository candleRepository) {
        this.candleRepository = candleRepository;
    }

    public void saveCandle(CandleEntity candle) { candleRepository.save(candle); }

    public void saveAll(List<CandleEntity> candles) {
        System.err.println("Saving " + candles.size() + " candles");
        for (CandleEntity candle : candles) {
            candleRepository.save(candle);
            cache.get(candle.getTicker()).compute(candle.getPeriod(),
                (key, candle1) -> candle1 == null || candle.getTimestamp() > candle1.getTimestamp() ? candle : candle1
            );
        }
    }

    public List<CandleEntityShort> getHistory(String ticker, long unit, long amount) {
        return candleRepository.findByTickerAndPeriodAndTimestampGreaterThan(
            ticker,
            unit,
            getCurrentTime() - unit * (amount + 2));
    }

    public long clearByTimestampAndPeriod(long timestamp, long period) {
        return candleRepository.deleteByTimestampLessThanAndPeriodLessThanEqual(timestamp, period);
    }

    public void informAboutTickers(Collection<String> tickers) {
        this.tickers.addAll(tickers);
        tickers.forEach(ticker -> cache.put(ticker, new HashMap<>()));
    }
    public Set<String> getTickers() { return tickers; }
    public Optional<CandleEntity> getCurrent(String ticker, long unit) {
        return Optional.ofNullable(cache.get(ticker).get(unit));
    }
}
