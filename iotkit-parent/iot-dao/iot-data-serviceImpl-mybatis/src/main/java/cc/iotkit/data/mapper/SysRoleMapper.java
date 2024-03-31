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

import cc.iotkit.data.model.TbSysRole;
import cc.iotkit.model.system.SysRole;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<TbSysRole> {

    List<Long> selectRoleListByUserId(@Param("userId") Long userId);

    List<TbSysRole> selectRolePermissionByUserId(@Param("userId") Long userId, @Param("delFlag") String delFlag);

    List<TbSysRole> selectRoleList(@Param("role") SysRole role, @Param("delFlag") String delFlag);

    List<TbSysRole> findByUserId(@Param("id") Long id);

}
