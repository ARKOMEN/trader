package org.ttrader.alwaysRun;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
public class BackgroundRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.err.println("Your app will RUN forever...");
        Thread.currentThread().join();
    }
}
