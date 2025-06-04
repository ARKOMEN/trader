package org.ttrader.newsService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;


@SpringBootApplication(scanBasePackages = {
    "org.ttrader.newsService",
    "org.ttrader.util",
    "org.ttrader.alwaysRun",
    "org.ttrader.config"
})
@Profile("news-service")
public class NewsApiMain {
    public static void main(String[] args) {
        System.err.println(NewsApiMain.class);
        SpringApplication.run(NewsApiMain.class, args);
    }
}
