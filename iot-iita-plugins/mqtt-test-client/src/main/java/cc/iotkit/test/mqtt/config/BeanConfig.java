package cc.iotkit.test.mqtt.config;


import cc.iotkit.test.mqtt.performance.ConnectionTest;
import cc.iotkit.test.mqtt.performance.ReportTest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class BeanConfig {

    @Bean
    @ConditionalOnProperty(name = "case", havingValue = "ConnectionTest")
    ConnectionTest getConnectionTest() {
        return new ConnectionTest();
    }

    @Bean
    @ConditionalOnProperty(name = "case", havingValue = "ReportTest")
    ReportTest getReportTest() {
        return new ReportTest();
    }
}
