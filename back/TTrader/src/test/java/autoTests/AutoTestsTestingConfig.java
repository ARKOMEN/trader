package autoTests;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.ttrader.mainService.mainClient.analysis.AnalysisClient;
import org.ttrader.mainService.repositories.CandleRepository;
import org.ttrader.mainService.repositories.NewsRepository;
import org.ttrader.mainService.repositories.TickerRepository;

@TestConfiguration
public class AutoTestsTestingConfig {
    @Bean
    public TickerRepository tickerRepository() {
        return Mockito.mock(TickerRepository.class);
    }
    @Bean
    public CandleRepository candleRepository() {
        return Mockito.mock(CandleRepository.class);
    }
    @Bean
    public NewsRepository newsRepository() {
        return Mockito.mock(NewsRepository.class);
    }
    @Bean
    public AnalysisClient analysisClient() {
        return Mockito.mock(AnalysisClient.class);
    }
}
