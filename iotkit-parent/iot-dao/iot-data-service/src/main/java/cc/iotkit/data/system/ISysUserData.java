package cc.iotkit.data.system;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.data.ICommonData;
import cc.iotkit.model.system.SysUser;

/**
 * 用户数据接口
 *
 * @author sjg
 */
public interface ISysUserData extends ICommonData<SysUser, Long> {

    /**
     * 按部门统计数量
     *
     * @param deptId 部门id
     * @return 数量
     */
    long countByDeptId(Long deptId);


    boolean checkUserNameUnique(SysUser to);

    boolean checkPhoneUnique(SysUser to);

    boolean checkEmailUnique(SysUser to);

    SysUser selectByPhonenumber(String phonenumber);

    SysUser selectTenantUserByPhonenumber(String phonenumber, String tenantId);

    SysUser selectTenantUserByEmail(String email, String tenantId);

    SysUser selectUserByEmail(String email);

    SysUser selectTenantUserByUserName(String username, String tenantId);

    SysUser selectUserByUserName(String username);


    Paging<SysUser> selectAllocatedList(PageRequest<SysUser> to);

    String selectUserPostGroup(String userName);

    String selectUserRoleGroup(String userName);



    Paging<SysUser> selectUnallocatedList(PageRequest<SysUser> to);

    SysUser findByPhonenumber(String phonenumber);
}
