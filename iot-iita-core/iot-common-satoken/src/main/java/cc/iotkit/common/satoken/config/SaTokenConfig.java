//package cc.iotkit.common.satoken.config;
//
//import cc.iotkit.common.satoken.core.dao.PlusSaTokenDao;
//import cc.iotkit.common.satoken.core.service.SaPermissionImpl;
//import cn.dev33.satoken.dao.SaTokenDao;
//import cn.dev33.satoken.interceptor.SaInterceptor;
//import cn.dev33.satoken.stp.StpInterface;
//import cn.dev33.satoken.stp.StpUtil;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * sa-token 配置
// *
// * @author Lion Li
// */
//@Configuration
//public class SaTokenConfig implements WebMvcConfigurer {
//
//    /**
//     * 权限接口实现(使用bean注入方便用户替换)
//     */
//    @Bean
//    public StpInterface stpInterface() {
//        return new SaPermissionImpl();
//    }
//
//    /**
//     * 自定义dao层存储
//     */
//    @Bean
//    public SaTokenDao saTokenDao() {
//        return new PlusSaTokenDao();
//    }
//
//    // 注册拦截器
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。
//
//        List<String> swaggerUrls = List.of("/doc.html","/favicon.ico", "/webjars/**", "/resources/**"
//                , "/swagger-resources/**", "/swagger-ui.html/**");
//
//        List loginUrls = List.of("/code", "/auth/tenant/list", "/auth/login", "/auth/logout","/iot-oss/**");
//        List<String> openApiUrls = List.of( "/openapi/v1/getToken");
//
//        List<String> excludeUrls = new ArrayList<>();
//        excludeUrls.addAll(loginUrls);
//        excludeUrls.addAll(swaggerUrls);
//        excludeUrls.addAll(openApiUrls);
//
//        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
//                .addPathPatterns("/**")
//                .excludePathPatterns(excludeUrls);
//    }
//}
