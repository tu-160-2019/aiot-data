package cc.iotkit.data.system;

import cc.iotkit.common.api.Paging;
import cc.iotkit.data.ICommonData;
import cc.iotkit.model.system.SysOperLog;

/**
 * 操作日志数据接口
 *
 * @author sjg
 */
public interface ISysOperLogData extends ICommonData<SysOperLog, Long> {

    /**
     * 按条件分页查询
     */
    Paging<SysOperLog> findByConditions(String tenantId, String title, Integer businessType,
                                        Integer status, int page, int size);

    /**
     * 按租户清除日志
     *
     * @param tenantId 租户id
     */
    void deleteByTenantId(String tenantId);

    void deleteAll();
}
