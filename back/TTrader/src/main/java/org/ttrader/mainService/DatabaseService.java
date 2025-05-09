package org.ttrader.mainService;

import org.springframework.stereotype.Service;
import org.ttrader.mainService.entities.CandleEntity;
import org.ttrader.mainService.repositories.CandleRepository;
import org.ttrader.util.HistoryType;

import java.time.Instant;
import java.util.List;

@Service
public class DatabaseService {
    private final CandleRepository candleRepository;

    private static long getCurrentTime() {
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
        candles.forEach(candleRepository::save);
    }

    public List<CandleEntity> getHistory(String ticker, HistoryType historyType) {
        //!!!graphType в historyType никак не влияет на результат метода
        return candleRepository
            .findByTickerAndPeriodAndTimestampGreaterThan(ticker, historyType.candlePeriod().getUnixPeriod(), getCurrentTime() - historyType.graphInterval())
            .stream().map(c -> new CandleEntity(ticker, c)).toList();
    }

}
