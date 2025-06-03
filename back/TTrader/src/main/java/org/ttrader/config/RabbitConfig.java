package org.ttrader.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue analysisQueue() {
        return new Queue("analysis");
    }
    @Bean
    public Queue newsQueue() {
        return new Queue("news");
    }
    @Bean
    public Queue candlesQueue() {
        return new Queue("stocks");
    }
}
