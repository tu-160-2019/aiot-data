package cc.iotkit.common.web.config;

import cc.iotkit.common.web.interceptor.TenantInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册租户ID拦截器.
 *
 * @author Tiger Chen
 * created on 2023/7/15 14:48
 */

@Configuration
public class TenantConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TenantInterceptor());
    }

}
