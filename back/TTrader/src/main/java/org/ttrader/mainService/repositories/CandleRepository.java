package org.ttrader.mainService.repositories;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Streamable;
import org.springframework.transaction.annotation.Transactional;
import org.ttrader.mainService.entities.CandleEntity;
import org.ttrader.mainService.entities.CandleEntityShort;
import org.ttrader.mainService.entities.TickerEntity;

import java.util.List;
import java.util.Optional;

@Profile("main-service")
public interface CandleRepository extends Repository<CandleEntity, Long> {
    CandleEntity save(CandleEntity candle);

//    @Modifying
//    @Transactional
//    @Query(
//    value = "insert into CandleEntity "+
//        "(open,  high,  low,  close,  ticker,  timestamp,  period) "+
//    "values "+
//        "(:open, :high, :low, :close, :ticker, :timestamp, :period)"
//    )
//    void save(@Param("open") double open, @Param("high") double high, @Param("low") double low, @Param("close") double close,
//              @Param("ticker") String ticker, @Param("timestamp") long timestamp, @Param("period") long period);
    //List<CandleEntity> saveAll(List<CandleEntity> candles);
    Optional<CandleEntity> findCandleEntityById(long id);
    Optional<CandleEntityShort> findCandleEntityShortById(long id);
    Streamable<CandleEntityShort> findByTickerAndTimestampGreaterThan(TickerEntity ticker, long beginTime);
    List<CandleEntityShort> findByTickerAndPeriodAndTimestampGreaterThan(TickerEntity ticker, long period, long beginTime);
    long deleteByTimestampLessThanAndPeriodLessThanEqual(long time, long period);
}
