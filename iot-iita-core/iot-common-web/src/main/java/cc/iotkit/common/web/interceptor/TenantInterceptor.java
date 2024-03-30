package cc.iotkit.common.web.interceptor;

import cn.dev33.satoken.context.SaHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 新增租户拦截器，拦截请求头中的租户id
 *
 * @author Tiger Chen
 * created on 2023/7/15 14:26
 */


public class TenantInterceptor implements HandlerInterceptor {

    public static final String TENANT_ID = "Tenant-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (request.getHeader(TENANT_ID) != null) {
            String tenantId = request.getHeader(TENANT_ID);
            SaHolder.getStorage().set("tenantId", tenantId);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
