package autoTests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.ttrader.analysisUtil.AnalysisAction;
import org.ttrader.analysisUtil.CommonAnalysis;
import org.ttrader.mainService.entities.NewsEntity;
import org.ttrader.mainService.entities.NewsShort;
import org.ttrader.mainService.entities.TickerEntity;
import org.ttrader.mainService.entities.TickerShort;
import org.ttrader.mainService.mainClient.analysis.AnalysisClient;
import org.ttrader.mainService.repositories.CandleRepository;
import org.ttrader.mainService.repositories.NewsRepository;
import org.ttrader.mainService.repositories.TickerRepository;
import org.ttrader.newsUtil.NewsDescriptor;
import util.TestCandle;


import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = AutoTestsAppConfig.class)
@ActiveProfiles({"analysis-service","main-service"})
public class AutoTests {

//    static Map<String, TickerEntity> tickerTestMap = Stream.of(
//        new TickerEntity("AAPL"), new TickerEntity("MSFT")
//    ).collect(Collectors.toMap(
//        TickerEntity::getTicker, t->t, (a, b)->null
//    ));
//
//    @Autowired
//    TickerRepository tickerRepository;
//
//    @Autowired
//    CandleRepository candleRepository;
//
//    @Autowired
//    NewsRepository newsRepository;
//
//    @Autowired
//    AnalysisClient analysisClient;
//
//    @Autowired
//    private MockMvc mvc;
//
//    private static final List<NewsEntity> newsList = generateNews(20);
//
//    private static List<NewsEntity> generateNews(int count) {
//        return LongStream.range(0, count).mapToObj(
//            i -> new NewsEntity(i ,i, tickerTestMap.get(i < 10 ? "AAPL" : "MSFT"), "title" + i, "descr" + i, "url" + i)
//        ).toList();
//    }
//
//    @BeforeEach
//    public void before() {
//        Mockito.when(tickerRepository.findAll()).thenReturn(
//            tickerTestMap.values().stream().map(t->(TickerShort)t).toList()
//        );
//
//        Mockito.when(newsRepository.findAllByOrderByTimeDesc(Mockito.any()))
//            .thenReturn(newsList.stream().map(n->(NewsShort)n).toList());
//
//        tickerTestMap.forEach((s,t)->
//            {
////                Mockito.when(tickerRepository.save(t)).thenThrow(
////                    new IOException("Ticker already exists, you stupid!")
////                );
//                Mockito.when(tickerRepository.findShortByTicker(s)).thenReturn(t);
//                Mockito.when(candleRepository.findByTickerAndPeriodAndTimestampGreaterThan(
//                    Mockito.any(), 10, Mockito.any()
//                )).thenReturn(List.of(
//                    new TestCandle(0, 0),
//                    new TestCandle(1, 0),
//                    new TestCandle(2, 0),
//                    new TestCandle(3, 0),
//                    new TestCandle(4, 0),
//                    new TestCandle(5, 0),
//                    new TestCandle(6, 0),
//                    new TestCandle(7, 0),
//                    new TestCandle(8, 0),
//                    new TestCandle(9, 0)
//                ));
//            }
//        );
//
//        Mockito.when(analysisClient.analyse(Mockito.any(), Mockito.any())).thenReturn(
//            new CommonAnalysis(AnalysisAction.BUY, 100)
//        );
//    }
//
//    @Test
//    public void recommendationsTest() throws Exception {
//        String result = mvc.perform(
//            get("/recomendations").contentType(MediaType.APPLICATION_JSON)
//        )
//            .andExpect(status().isOk())
//            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
//            .andReturn().getResponse().getContentAsString();
//
//        JsonNode node = new ObjectMapper().readTree(result);
//
//        Assertions.

//    }


}
