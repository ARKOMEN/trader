package org.ttrader.mainService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.ttrader.mainService", "org.ttrader.secondaryServicesUtil"})
public class MainServiceMain {
    public static void main(String[] args) {
        SpringApplication.run(MainServiceMain.class, args);
    }
}