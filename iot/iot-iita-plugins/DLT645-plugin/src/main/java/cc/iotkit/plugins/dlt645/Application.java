package cc.iotkit.plugins.dlt645;

import com.gitee.starblues.bootstrap.SpringPluginBootstrap;
import com.gitee.starblues.bootstrap.annotation.OneselfConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Author：tfd
 * @Date：2023/12/14 16:25
 */
@SpringBootApplication(scanBasePackages = "cc.iotkit.plugins.dlt645")
@OneselfConfig(mainConfigFileName = {"application.yml"})
@EnableConfigurationProperties
@EnableScheduling
public class Application extends SpringPluginBootstrap {
    public static void main(String[] args) {
        new Application().run(Application.class, args);
    }
}
