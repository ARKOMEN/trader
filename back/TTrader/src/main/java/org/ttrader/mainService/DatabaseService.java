package org.ttrader.mainService;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.ttrader.mainService.repositories.CandleRepository;
import org.ttrader.util.HistoryType;
import org.ttrader.mainService.entities.CandleEntity;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Profile("main-service")
public class DatabaseService {
    private final CandleRepository candleRepository;

    private final Set<String> tickers = new HashSet<>();

    public static long getCurrentTime() {
        return Instant.now().getEpochSecond();
    }

    public DatabaseService(CandleRepository candleRepository) {
        this.candleRepository = candleRepository;
    }

    public void saveCandle(CandleEntity candle) { candleRepository.save(candle); }

//    public void saveCandle(CandleEntityFull candle) {
//        candleRepository.save(new CandleEntity(candle));
//    }
//
//    public void saveCandle(String ticker, CandleEntityShort candle) {
//        candleRepository.save(new CandleEntity(ticker, candle));
//    }

    public void saveAll(List<CandleEntity> candles) {
        System.err.println("Saving " + candles.size() + " candles");
        candles.forEach(candleRepository::save);
    }

    public List<CandleEntity> getHistory(String ticker, HistoryType historyType) {
        //!!!graphType в historyType никак не влияет на результат метода
        return candleRepository
            .findByTickerAndPeriodAndTimestampGreaterThan(ticker, historyType.candlePeriod().getUnixPeriod(), getCurrentTime() - historyType.graphInterval())
            .stream().map(c -> new CandleEntity(ticker, c)).toList();
    }

    public long clearByTimestampAndPeriod(long timestamp, long period) {
        return candleRepository.deleteByTimestampLessThanAndPeriodLessThanEqual(timestamp, period);
    }

    public void informAboutTickers(Collection<String> tickers) {
        this.tickers.addAll(tickers);
    }

}
