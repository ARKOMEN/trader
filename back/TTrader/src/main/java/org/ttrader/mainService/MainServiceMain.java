package org.ttrader.mainService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "org.ttrader.mainService",
    "org.ttrader.util",
    "org.ttrader.config"
})
public class MainServiceMain {
    public static void main(String[] args) {
        System.err.println(MainServiceMain.class);
        SpringApplication.run(MainServiceMain.class, args);
    }
}