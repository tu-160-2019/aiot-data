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

import cc.iotkit.data.model.TbSysMenu;

import cc.iotkit.model.system.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<TbSysMenu> {

    List<String> selectMenuPermsByRoleId(@Param("roleId") Long roleId);

    List<TbSysMenu> selectMenuTreeByUserId(@Param("userId") Long userId);

    List<String> selectMenuPermsByUserId(@Param("userId") Long userId);

    List<TbSysMenu> selectMenuList(@Param("menu") SysMenu menu, @Param("userId") Long userId);

    List<Long> listParentIdByRoleId(@Param("roleId") Long roleId);

    List<Long> selectMenuListByRoleId(@Param("roleIds") List<Long> roleIds, @Param("menuCheckStrictly") boolean menuCheckStrictly, @Param("parentRoleId") Long parentRoleId);

}
