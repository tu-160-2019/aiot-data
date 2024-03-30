package cc.iotkit.data.system;

import cc.iotkit.data.ICommonData;
import cc.iotkit.model.system.SysRoleMenu;

import java.util.Collection;
import java.util.List;

/**
 * 操作日志数据接口
 *
 * @author sjg
 */
public interface ISysRoleMenuData extends ICommonData<SysRoleMenu, Long> {
    boolean checkMenuExistRole(Long menuId);

    long insertBatch(List<SysRoleMenu> list);

    long deleteByRoleId(Collection<Long> ids);

}
