package org.ttrader.analysisService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication(scanBasePackages = {
    "org.ttrader.analysisService",
    "org.ttrader.analysisUtil"
})
@Profile("analysis-service")
public class AnalysisMain {
    public static void main(String[] args) {
        System.err.println(AnalysisMain.class);
        SpringApplication.run(AnalysisMain.class, args);
    }
}
