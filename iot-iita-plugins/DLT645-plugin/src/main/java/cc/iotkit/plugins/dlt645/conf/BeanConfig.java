package cc.iotkit.plugins.dlt645.conf;

import cc.iotkit.plugin.core.IPluginConfig;
import cc.iotkit.plugin.core.IPluginScript;
import cc.iotkit.plugin.core.LocalPluginConfig;
import cc.iotkit.plugin.core.LocalPluginScript;
import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugins.dlt645.service.FakeThingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Author：tfd
 * @Date：2023/12/13 16:54
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
