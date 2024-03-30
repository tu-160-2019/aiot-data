package cc.iotkit.data.system;

import cc.iotkit.data.ICommonData;
import cc.iotkit.model.system.SysRoleDept;

import java.util.Collection;
import java.util.List;

/**
 * 操作日志数据接口
 *
 * @author sjg
 */
public interface ISysRoleDeptData extends ICommonData<SysRoleDept, Long> {

    void deleteByRoleId(Collection<Long> roleIds);

    long insertBatch(List<SysRoleDept> list);

}
