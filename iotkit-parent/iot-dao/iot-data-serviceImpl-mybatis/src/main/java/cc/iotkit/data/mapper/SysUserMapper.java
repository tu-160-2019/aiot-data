/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.data.mapper;

import cc.iotkit.data.model.TbSysUser;
import cc.iotkit.model.system.SysUser;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface SysUserMapper extends BaseMapper<TbSysUser> {

    List<String> selectUserPostGroup(@Param("userName") String userName);

    List<String> selectUserRoleGroup(@Param("userName") String userName);

    List<TbSysUser> selectAllocatedList(@Param("sysUser") SysUser sysUser,
                                        @Param("delFlag") String delFlag,
                                        @Param("offset") Integer offset, @Param("limit") Integer limit);

    Long selectAllocatedListCount(@Param("sysUser") SysUser sysUser, @Param("delFlag") String delFlag);

    List<TbSysUser> listWithDeptId(@Param("sysUser") SysUser sysUser);

}
