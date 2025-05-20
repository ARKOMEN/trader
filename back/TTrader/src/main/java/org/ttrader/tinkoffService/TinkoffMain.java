package org.ttrader.tinkoffService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Profile("tinkoff-service")
public class TinkoffMain {

    public static void main(String[] args) {
        SpringApplication.run(TinkoffMain.class, args);
    }

}
