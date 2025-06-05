package org.ttrader.mainService;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.ttrader.mainService.analisys.CompanyDescriptor;
import org.ttrader.mainService.analisys.SpecialAnalysis;
import org.ttrader.mainService.entities.CandleEntityFull;
import org.ttrader.mainService.entities.CandleEntityShort;
import org.ttrader.mainService.entities.NewsShort;
import org.ttrader.mainService.mainClient.analysis.AnalysisClient;

import java.util.*;

@Profile("main-service")
@Service
public class UserService {
    private final DatabaseService databaseService;
    private final AnalysisClient analysisClient;

    public UserService(DatabaseService databaseService, AnalysisClient analysisClient) {
        this.databaseService = databaseService;
        this.analysisClient = analysisClient;
    }

    public Set<String> getTickers(String correlationId) {
        System.err.println(correlationId + " : [user service] retrieving tickers...");
        return databaseService.getAllTickers(correlationId);
    }

    public List<CandleEntityShort> getHistory(String correlationId, String ticker, long unit, long amount) {
        System.err.println(correlationId + " : [user service] retrieving history...");
        return databaseService.getHistory(correlationId, ticker, unit, amount);
    }

    public Optional<CandleEntityFull> getCurrent(String correlationId, String ticker, long unit) {
        System.err.println(correlationId + " : [user service] retrieving current price info...");
        return databaseService.getCurrent(correlationId, ticker, unit);
    }

    public List<SpecialAnalysis> getRecomendations(String correlationId) {
        System.err.println(correlationId + " : [user service] retrieving recommendations...");
        return databaseService.getAllTickers(correlationId).stream().map(
            ticker -> new SpecialAnalysis(ticker, analysisClient.analyse(correlationId,
                databaseService.getHistory(correlationId, ticker, 10, 100)
            ))
        ).sorted(
            (o1, o2) -> o2.confidence() - o1.confidence()
        ).toList();
    }

    public SpecialAnalysis singleAnalysis(String correlationId, String ticker) {
        System.err.println(correlationId + " : [user service] retrieving single analysis info...");
        return new SpecialAnalysis(
            ticker, analysisClient.analyse(
                correlationId,
                databaseService.getHistory(correlationId, ticker, 10, 100)
            )
        );
    }

    public CompanyDescriptor getCompany(String correlationId, String ticker) {
        System.err.println(correlationId + " : [user service] retrieving company info...");
        return databaseService.getCompany(correlationId, ticker);
    }

    public List<NewsShort> getNews(String correlationId) {
        System.err.println(correlationId + " : [user service] retrieving news...");
        return databaseService.getLastNews(correlationId);
    }

}
