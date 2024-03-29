package cc.iotkit.temporal.iotdb.config;


import org.apache.iotdb.session.pool.SessionPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * iotdb数据库配置
 *
 * @author sjg
 */
@Configuration
public class IotDbConf {

    @Value("${spring.iotdb-datasource.host}")
    private String host;

    @Value("${spring.iotdb-datasource.port}")
    private int port;

    @Value("${spring.iotdb-datasource.username}")
    private String username;

    @Value("${spring.iotdb-datasource.password}")
    private String password;

    @Bean
    public SessionPool getSession() {
        return new SessionPool.Builder()
                .host(host)
                .port(port)
                .user(username)
                .password(password)
                .build();
    }
}
