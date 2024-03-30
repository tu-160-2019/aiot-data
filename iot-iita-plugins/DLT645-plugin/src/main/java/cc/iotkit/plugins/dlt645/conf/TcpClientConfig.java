package cc.iotkit.plugins.dlt645.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author：tfd
 * @Date：2023/12/13 17:01
 */
@Data
@Component
@ConfigurationProperties(prefix = "tcp")
public class TcpClientConfig {

    private String host;

    private int port;

    private int interval;
}
