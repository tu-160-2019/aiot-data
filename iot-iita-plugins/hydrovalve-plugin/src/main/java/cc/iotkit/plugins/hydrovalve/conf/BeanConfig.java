package cc.iotkit.plugins.hydrovalve.conf;

import cc.iotkit.plugin.core.IPluginConfig;
import cc.iotkit.plugin.core.IPluginScript;
import cc.iotkit.plugin.core.LocalPluginConfig;
import cc.iotkit.plugin.core.LocalPluginScript;
import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugins.hydrovalve.service.FakeThingService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Author：tfd
 * @Date：2024/1/8 14:58
 */
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
        return new LocalPluginScript("script.js");
    }

    @Bean
    @ConditionalOnProperty(name = "plugin.runMode", havingValue = "dev")
    IPluginConfig getPluginConfig(){
        return new LocalPluginConfig();
    }
}
