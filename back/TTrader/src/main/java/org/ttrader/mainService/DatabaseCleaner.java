package org.ttrader.mainService;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.ttrader.util.CandlePeriod;

@Component
@EnableScheduling
@Profile("main-service")
public class DatabaseCleaner {

    private final DatabaseService databaseService;

    private final CandlePeriod[] periodsReversed;

    @Scheduled(cron = "${ttrader.db_cleaner.update_time}", zone = "${ttrader.time_zone}")
    public void cleanup() {
        long current = DatabaseService.getCurrentTime();
        for (CandlePeriod period : periodsReversed) {
            databaseService.clearByTimestampAndPeriod(current - period.getLiveTime(), period.getUnixPeriod());
        }
    }


    public DatabaseCleaner(DatabaseService databaseService) {
        this.databaseService = databaseService;
        periodsReversed = CandlePeriod.allButLast.clone();
        for (int i = (periodsReversed.length+1)/2; i < periodsReversed.length; ++i) {
            CandlePeriod temp = periodsReversed[periodsReversed.length - i];
            periodsReversed[periodsReversed.length - i] = periodsReversed[i];
            periodsReversed[i] = temp;
        }
    }

}
