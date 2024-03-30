package cc.iotkit.data.system;


import cc.iotkit.data.ICommonData;
import cc.iotkit.model.system.SysDept;

import java.util.List;

/**
 * 部门数据接口
 *
 * @author sjg
 */
public interface ISysDeptData extends ICommonData<SysDept, Long> {




    /**
     * 复合备件查询
     *
     * @param dept dept
     * @return list
     */
    List<SysDept> findDepts(SysDept dept);

    /**
     * 根据角色ID查询部门树信息
     *
     * @param roleId 角色ID
     * @return 选中部门列表
     */
    List<SysDept> findByRoleId(Long roleId);

    /**
     * 根据ID查询所有子部门数（正常状态）
     *
     * @param parentId 部门ID
     * @return 子部门数
     */
    long countByParentId(Long parentId);

    /**
     * 根据ID查询所有子部门数（所有状态）
     *
     * @param deptId 部门ID
     * @return 部门列表
     */
    List<SysDept> findByDeptId(Long deptId);


    boolean checkDeptNameUnique(String deptName, Long parentId, Long deptId);

    long selectNormalChildrenDeptById(Long deptId);
}
