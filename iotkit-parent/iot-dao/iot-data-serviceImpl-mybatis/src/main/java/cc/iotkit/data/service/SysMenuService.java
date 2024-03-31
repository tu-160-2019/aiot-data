package cc.iotkit.data.service;

import cc.iotkit.data.model.TbSysMenu;
import cc.iotkit.model.system.SysMenu;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysMenuService extends IService<TbSysMenu> {
    List<TbSysMenu> findByConditions(LambdaQueryWrapper<TbSysMenu> lambdaQueryWrapper);

    TbSysMenu findOneByConditions(LambdaQueryWrapper<TbSysMenu> lambdaQueryWrapper);

    Page<TbSysMenu> findPageByConditions(LambdaQueryWrapper<TbSysMenu> lambdaQueryWrapper, int page, int size);

    List<String> selectMenuPermsByRoleId(Long roleId);

    List<TbSysMenu> selectMenuTreeByUserId(Long userId);

    List<String> selectMenuPermsByUserId(Long userId);

    List<TbSysMenu> selectMenuList(SysMenu menu, Long userId);

    List<Long> listParentIdByRoleId(Long roleId);

    List<Long> selectMenuListByRoleId(List<Long> roleIds, boolean menuCheckStrictly, Long parentRoleId);
}
