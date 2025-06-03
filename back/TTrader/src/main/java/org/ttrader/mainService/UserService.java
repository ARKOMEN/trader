package org.ttrader.mainService;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.ttrader.mainService.analisys.CompanyDescriptor;
import org.ttrader.mainService.analisys.SpecialAnalysis;
import org.ttrader.mainService.entities.CandleEntityFull;
import org.ttrader.mainService.entities.CandleEntityShort;
import org.ttrader.mainService.entities.NewsShort;
import org.ttrader.mainService.mainClient.AnalysisClient;

import java.util.*;
import java.util.stream.Collectors;

@Profile("main-service")
@Service
public class UserService {
    private final DatabaseService databaseService;
    private final AnalysisClient analysisClient;

    public UserService(DatabaseService databaseService, AnalysisClient analysisClient) {
        this.databaseService = databaseService;
        this.analysisClient = analysisClient;
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

    List<SpecialAnalysis> getRecomendations() {
        return databaseService.getTickers().stream().map(
            ticker -> new SpecialAnalysis(ticker, analysisClient.analyse(
                databaseService.getHistory(ticker, 10, 100)
            ))
        ).sorted(
            (o1, o2) -> o2.confidence() - o1.confidence()
        ).toList();
    }

    SpecialAnalysis singleAnalysis(String ticker) {
        return new SpecialAnalysis(
            ticker, analysisClient.analyse(databaseService.getHistory(ticker, 10, 100))
        );
    }

    CompanyDescriptor getCompany(String ticker) {
        return databaseService.getCompany(ticker);
    }

    List<NewsShort> getNews() {
        return databaseService.getLastNews();
    }

}
