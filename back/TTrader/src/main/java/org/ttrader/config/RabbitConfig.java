package org.ttrader.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {


    @Bean(name = "news-exchange")
    public FanoutExchange newsExchange() {
        return new FanoutExchange("news");
    }

    @Bean(name = "news-queue")
    public Queue newsQueue() {
        return new AnonymousQueue();
    }

    @Bean(name = "news-binding")
    public Binding newsBinding(@Qualifier("news-exchange") FanoutExchange exchange, @Qualifier("news-queue") Queue ephemeralQueue) {
        return BindingBuilder.bind(ephemeralQueue).to(exchange);
    }

    @Bean(name = "analysis-exchange")
    public FanoutExchange analysisExchange() {
        return new FanoutExchange("analysis");
    }

    @Bean(name = "analysis-queue")
    public Queue analysisQueue() {
        return new AnonymousQueue();
    }

    @Bean(name = "analysis-binding")
    public Binding analysisBinding(@Qualifier("analysis-exchange") FanoutExchange exchange, @Qualifier("analysis-queue") Queue ephemeralQueue) {
        return BindingBuilder.bind(ephemeralQueue).to(exchange);
    }

    @Bean(name = "stocks-exchange")
    public FanoutExchange stocksExchange() {
        return new FanoutExchange("stocks");
    }

    @Bean(name = "stocks-queue")
    public Queue stocksQueue() {
        return new AnonymousQueue();
    }

    @Bean(name = "stocks-binding")
    public Binding stocksBinding(@Qualifier("stocks-exchange") FanoutExchange exchange, @Qualifier("stocks-queue") Queue ephemeralQueue) {
        return BindingBuilder.bind(ephemeralQueue).to(exchange);
    }
}
