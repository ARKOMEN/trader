package org.ttrader.mainService;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.ttrader.mainService.entities.CandleEntity;
import org.ttrader.mainService.entities.CandleEntityFull;
import org.ttrader.mainService.entities.CandleEntityShort;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Profile("main-service")
@Service
public class UserService {
    private final DatabaseService databaseService;

    public UserService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    Set<String> getTickers() {
        return databaseService.getTickers();
    }

    List<CandleEntityShort> getHistory(String ticker, long unit, long amount) {
        return databaseService.getHistory(ticker, unit, amount);
    }

    Optional<CandleEntityFull> getCurrent(String ticker, long unit) {
        return databaseService.getCurrent(ticker, unit);
    }

}
