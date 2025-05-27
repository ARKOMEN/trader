package org.ttrader.alwaysRun;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
public class BackgroundRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Test profile active. Running background process...");
        Thread.currentThread().join(); // Блокирует текущий поток навсегда
    }
}
