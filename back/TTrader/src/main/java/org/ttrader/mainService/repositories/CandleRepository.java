package org.ttrader.mainService.repositories;

import org.springframework.data.repository.Repository;
import org.springframework.data.util.Streamable;
import org.ttrader.mainService.entities.CandleEntity;
import org.ttrader.mainService.entities.CandleEntityShort;

import java.util.Optional;

public interface CandleRepository extends Repository<CandleEntity, Long> {
    CandleEntity save(CandleEntity candle);
    //List<CandleEntity> saveAll(List<CandleEntity> candles);
    Optional<CandleEntity> findCandleEntityById(long id);
    Optional<CandleEntityShort> findCandleEntityShortById(long id);
    Streamable<CandleEntityShort> findByTickerAndTimestampGreaterThan(String ticker, long beginTime);
    Streamable<CandleEntityShort> findByTickerAndPeriodAndTimestampGreaterThan(String ticker, long period, long beginTime);
}
