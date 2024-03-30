package cc.iotkit.plugins.tcp.conf;

import cc.iotkit.plugin.core.IPluginConfig;
import cc.iotkit.plugin.core.IPluginScript;
import cc.iotkit.plugin.core.LocalPluginConfig;
import cc.iotkit.plugin.core.LocalPluginScript;
import cc.iotkit.plugin.core.thing.IThingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author sjg
 */
@Slf4j
@Component
public class BeanConfig {

    @Bean
    @ConditionalOnProperty(name = "plugin.runMode", havingValue = "dev")
    IThingService getThingService() {
        return new FakeThingService();
    }

    @Bean
    @ConditionalOnProperty(name = "plugin.runMode", havingValue = "dev")
    IPluginScript getPluginScript() {
        log.info("init LocalPluginScript");
        return new LocalPluginScript("script.js");
    }

    @Bean
    @ConditionalOnProperty(name = "plugin.runMode", havingValue = "dev")
    IPluginConfig getPluginConfig() {
        return new LocalPluginConfig();
    }
}
