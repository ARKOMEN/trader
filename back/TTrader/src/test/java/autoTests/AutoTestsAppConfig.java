package autoTests;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

@Configuration
@TestPropertySource(locations = "/analysis-test.properties")
@ComponentScan(basePackages = {"org.ttrader.mainService.mainClient"})
@ImportAutoConfiguration({
    RabbitAutoConfiguration.class
})
public class AutoTestsAppConfig {

}
