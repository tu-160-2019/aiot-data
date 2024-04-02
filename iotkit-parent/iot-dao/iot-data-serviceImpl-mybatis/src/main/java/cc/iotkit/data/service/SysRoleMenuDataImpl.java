package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.model.TbSysRoleMenu;
import cc.iotkit.data.system.ISysRoleMenuData;
import cc.iotkit.model.system.SysRoleMenu;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;


/**
 * author: 石恒
 * date: 2023-05-30 11:00
 * description:
 **/
@Primary
@Service
@RequiredArgsConstructor
public class SysRoleMenuDataImpl implements ISysRoleMenuData, IJPACommData<SysRoleMenu, Long> {
//public class SysRoleMenuDataImpl implements ISysRoleMenuData, IJPACommData<SysRoleMenu, Long, TbSysRoleMenu> {


    private final SysRoleMenuService sysRoleMenuService;

    @Override
    public SysRoleMenuService getBaseRepository() {
        return sysRoleMenuService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysRoleMenu.class;
    }

    @Override
    public Class getTClass() {
        return SysRoleMenu.class;
    }

    @Override
    public boolean checkMenuExistRole(Long menuId) {
        return sysRoleMenuService.count(new LambdaQueryWrapper<TbSysRoleMenu>()
                .eq(TbSysRoleMenu::getMenuId, menuId)) > 0;
    }

    @Override
    public long insertBatch(List<SysRoleMenu> list) {
        List<TbSysRoleMenu> tbSysRoleMenus = Objects.requireNonNull(MapstructUtils.convert(list, TbSysRoleMenu.class));
        return sysRoleMenuService.saveBatch(tbSysRoleMenus) ? list.size() : 0L;
    }

    @Override
    public long deleteByRoleId(Collection<Long> ids) {
        boolean res = sysRoleMenuService.remove(new LambdaQueryWrapper<TbSysRoleMenu>()
                .in(TbSysRoleMenu::getRoleId, ids));
        return res ? ids.size() : 0L;
    }


}
