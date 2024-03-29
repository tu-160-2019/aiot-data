package cc.iotkit.data.system;

import cc.iotkit.data.ICommonData;
import cc.iotkit.model.system.SysUserRole;

import java.util.List;

/**
 * 用户角色数据接口
 *
 * @author sjg
 */
public interface ISysUserRoleData extends ICommonData<SysUserRole, Long> {
    /**
     * 按用户id删除数据
     *
     * @param userId 用户id
     * @return 数量
     */
    int deleteByUserId(Long userId);

    /**
     * 通过角色ID查询角色使用数量
     *
     * @param roleId 角色ID
     * @return 结果
     */
    long countUserRoleByRoleId(Long roleId);

    long delete(Long roleId, List<Long> userIds);

    long insertBatch(List<SysUserRole> list);
}
