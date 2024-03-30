package cc.iotkit.plugins.hydrovalve.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author：tfd
 * @Date：2024/1/8 15:04
 */
@Data
@Component
@ConfigurationProperties(prefix = "hydrovalve")
public class HydrovalveConfig {

    private String host;

    private int port;

    private int interval;
}
