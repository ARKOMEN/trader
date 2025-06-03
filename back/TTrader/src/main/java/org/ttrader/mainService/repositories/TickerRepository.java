package org.ttrader.mainService.repositories;

import org.springframework.context.annotation.Profile;

import org.springframework.data.repository.Repository;
import org.ttrader.mainService.entities.CandleEntity;
import org.ttrader.mainService.entities.TickerEntity;
import org.ttrader.mainService.entities.TickerShort;

import java.util.List;

@Profile("main-service")
public interface TickerRepository extends Repository<TickerEntity, Long> {
    CandleEntity save(TickerEntity ticker);
    List<TickerShort> findAll();
}
