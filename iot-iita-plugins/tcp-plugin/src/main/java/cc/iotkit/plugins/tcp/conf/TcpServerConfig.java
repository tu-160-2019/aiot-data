package cc.iotkit.plugins.tcp.conf;

import io.vertx.core.net.SocketAddress;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author huangwenl
 * @date 2022-10-13
 */
@Data
@Component
@ConfigurationProperties(prefix = "tcp")
public class TcpServerConfig {

    private String host;

    private int port;

    /**
     * 服务实例数量(线程数)
     */
    private int instance = Runtime.getRuntime().availableProcessors();

    public SocketAddress createSocketAddress() {
        if (StringUtils.isEmpty(host)) {
            host = "localhost";
        }
        return SocketAddress.inetSocketAddress(port, host);
    }

}
