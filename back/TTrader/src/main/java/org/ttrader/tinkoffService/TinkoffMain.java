package org.ttrader.tinkoffService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication(scanBasePackages = {
    "org.ttrader.tinkoffService",
    "org.ttrader.secondaryServicesUtil",
    "org.ttrader.util",
    "org.ttrader.alwaysRun"
})
@Profile("tinkoff-service")
public class TinkoffMain {

    public static void main(String[] args) {
        System.err.println(TinkoffMain.class);
        SpringApplication.run(TinkoffMain.class, args);
    }

}
