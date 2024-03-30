package cc.iotkit.screen.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author：tfd
 * @Date：2023/6/25 16:04
 */
@Configuration
@Data
public class ScreenConfig {

    @Value("${bigScreen.dir:./data/screens}")
    private String screenDir;
    @Value("${bigScreen.admin:/iotkit/screen}")
    public String screenAdmin;

    public Path getBigScreenFilePath(String screenId) {
        return Paths.get(screenDir, screenId)
                .toAbsolutePath().normalize();
    }
}
