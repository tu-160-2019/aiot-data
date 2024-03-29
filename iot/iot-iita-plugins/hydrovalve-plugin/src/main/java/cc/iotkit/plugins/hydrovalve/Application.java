package cc.iotkit.plugins.hydrovalve;

import com.gitee.starblues.bootstrap.SpringPluginBootstrap;
import com.gitee.starblues.bootstrap.annotation.OneselfConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Author：tfd
 * @Date：2024/1/8 14:57
 */
@SpringBootApplication(scanBasePackages = "cc.iotkit.plugins.hydrovalve")
@OneselfConfig(mainConfigFileName = {"application.yml"})
@EnableConfigurationProperties
@EnableScheduling
public class Application extends SpringPluginBootstrap {

    public static void main(String[] args) {
        new Application().run(Application.class, args);
    }
}
