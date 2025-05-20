package org.ttrader.finnhubService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Profile("finnhub-service")
public class FinnhubMain {

    public static void main(String[] args) {
        SpringApplication.run(FinnhubMain.class, args);
    }

}
