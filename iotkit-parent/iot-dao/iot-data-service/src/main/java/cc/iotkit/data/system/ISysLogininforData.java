package cc.iotkit.data.system;

import cc.iotkit.data.ICommonData;
import cc.iotkit.common.api.Paging;
import cc.iotkit.model.system.SysLoginInfo;

import java.util.List;

/**
 * 登录记录数据接口
 *
 * @author sjg
 */
public interface ISysLogininforData extends ICommonData<SysLoginInfo, Long> {

    /**
     * 按条件查询
     */
    List<SysLoginInfo> findByConditions(SysLoginInfo cond);

    /**
     * 按条件分页查询
     */
    Paging<SysLoginInfo> findByConditions(SysLoginInfo cond, int page, int size);

    /**
     * 按租户清除登录日志
     *
     * @param tenantId 租户id
     */
    void deleteByTenantId(String tenantId);

    /**
     * 清除所有
     */
    void deleteAll();
}
