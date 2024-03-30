package cc.iotkit.data.config;

import cc.iotkit.common.satoken.utils.LoginHelper;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * @Author：tfd
 * @Date：2024/1/12 15:06
 */
@Configuration
public class UserIDAuditorConfig implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        Long userId = LoginHelper.getUserId();
        return Optional.of(userId == null ? 1 : userId);
    }
}
