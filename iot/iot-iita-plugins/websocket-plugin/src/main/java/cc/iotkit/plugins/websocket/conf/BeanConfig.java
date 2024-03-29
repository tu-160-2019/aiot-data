package cc.iotkit.plugins.websocket.conf;

import cc.iotkit.plugin.core.IPluginConfig;
import cc.iotkit.plugin.core.LocalPluginConfig;
import cc.iotkit.plugin.core.thing.IThingService;
import cc.iotkit.plugins.websocket.service.FakeThingService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author tfd
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
    IPluginConfig getPluginConfig(){
        return new LocalPluginConfig();
    }
}
