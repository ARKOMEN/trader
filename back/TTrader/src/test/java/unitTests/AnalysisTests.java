package unitTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.ttrader.analysisService.AnalysisController;
import org.ttrader.analysisUtil.AnalysisAction;
import org.ttrader.analysisUtil.CommonAnalysis;
import org.ttrader.mainService.entities.CandleEntityShort;
import org.ttrader.mainService.mainClient.analysis.AnalysisClient;
import util.TestCandle;

import java.util.List;
import java.util.UUID;

@SpringBootTest(classes = AnalysisTestsConfig.class)
@ActiveProfiles({"analysis-service","main-service"})
public class AnalysisTests {

    @Autowired
    private AnalysisClient analysisClient;
    @Autowired
    private AnalysisController analysisController;

    @Test
    public void analysisBuy() {
        CommonAnalysis commonAnalysis = analysisClient.analyse(UUID.randomUUID().toString(), List.of(
            new TestCandle(10, 0),
            new TestCandle(10, 1),
            new TestCandle(10, 2),
            new TestCandle(10, 2),
            new TestCandle(10, 3),
            new TestCandle(10, 4),
            new TestCandle(10, 5),
            new TestCandle(10, 6),
            new TestCandle(10, 7),
            new TestCandle(10, 8),
            new TestCandle(11, 9),
            new TestCandle(12, 10),
            new TestCandle(12, 11),
            new TestCandle(13, 12),
            new TestCandle(14, 13),
            new TestCandle(14, 14),
            new TestCandle(10, 15),
            new TestCandle(11, 16),
            new TestCandle(12, 17),
            new TestCandle(13, 18),
            new TestCandle(16, 19)
        ));
        Assertions.assertEquals(AnalysisAction.BUY, commonAnalysis.action());
    }

    @Test
    public void analysisNone() {
        CommonAnalysis commonAnalysis = analysisClient.analyse(UUID.randomUUID().toString(), List.of());
        Assertions.assertEquals(commonAnalysis.action(), AnalysisAction.HOLD);
        Assertions.assertEquals(commonAnalysis.confidence(), 0);
    }
}
