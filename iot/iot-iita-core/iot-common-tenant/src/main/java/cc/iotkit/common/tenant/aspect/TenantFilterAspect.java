package cc.iotkit.common.tenant.aspect;


import cc.iotkit.common.satoken.utils.LoginHelper;
import cc.iotkit.common.tenant.helper.TenantHelper;
import cc.iotkit.common.utils.StringUtils;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.Session;

/**
 * 类描述...
 *
 * @author Tiger Chen
 * created on 2023/7/14 20:53
 */

@Aspect
public class TenantFilterAspect {

    @Pointcut("execution (* org.hibernate.internal.SessionFactoryImpl.SessionBuilderImpl.openSession(..))")
    public void openSession() {
    }

    @AfterReturning(pointcut = "openSession()", returning = "session")
    public void afterOpenSession(Object session) {
        if (session instanceof Session) {
            String tenantId = LoginHelper.getTenantId();
            String dynamic = TenantHelper.getDynamic();
            if (StringUtils.isNotBlank(dynamic)) {
                tenantId = dynamic;
            }
            if (tenantId != null && !tenantId.equals("000000")) {
                org.hibernate.Filter filter = ((Session) session).enableFilter("tenantFilter");
                filter.setParameter("tenantId", tenantId);
            }
        }
    }

}
