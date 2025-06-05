package unitTests;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@Configuration
@TestPropertySource(locations = "/analysis-test.properties")
@ComponentScan(basePackages = {"org.ttrader.mainService.mainClient.analysis",
    "org.ttrader.analysisService"})
@ImportAutoConfiguration({
    RabbitAutoConfiguration.class
})
public class AnalysisTestsConfig {
//    @Bean(name = "analysis-exchange")
//    public FanoutExchange analysisExchange() {
//        return new FanoutExchange("analysis");
//    }
//
//    @Bean(name = "analysis-queue")
//    public Queue analysisQueue() {
//        return new AnonymousQueue();
//    }
//
//    @Bean(name = "analysis-binding")
//    public Binding analysisBinding(@Qualifier("analysis-exchange") FanoutExchange exchange, @Qualifier("analysis-queue") Queue ephemeralQueue) {
//        return BindingBuilder.bind(ephemeralQueue).to(exchange);
//    }
}
