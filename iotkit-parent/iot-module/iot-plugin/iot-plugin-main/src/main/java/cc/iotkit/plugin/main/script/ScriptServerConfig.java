package cc.iotkit.plugin.main.script;

import cn.hutool.core.util.RandomUtil;
import io.vertx.core.net.SocketAddress;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

/**
 * @author sjg
 */
public class ScriptServerConfig {
    private static int _port;

    static {
        _port = RandomUtil.randomInt(11024, 12024);
    }

    @Getter
    private String host = "localhost";

    @Getter
    private int port = _port;

    public SocketAddress createSocketAddress() {
        if (StringUtils.isEmpty(host)) {
            host = "localhost";
        }
        return SocketAddress.inetSocketAddress(port, host);
    }
}
