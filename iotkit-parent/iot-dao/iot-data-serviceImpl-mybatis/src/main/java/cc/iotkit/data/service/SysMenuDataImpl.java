package cc.iotkit.data.service;

import cc.iotkit.common.constant.UserConstants;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.model.TbSysMenu;
import cc.iotkit.data.system.ISysMenuData;

import cc.iotkit.model.system.SysMenu;
import cn.hutool.core.util.ObjectUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;




/**
 * @Author: 石恒
 * @Date: 2023/5/28 15:43
 * @Description:
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysMenuDataImpl implements ISysMenuData, IJPACommData<SysMenu, Long, TbSysMenu> {

    @Qualifier("DBSysMenuServiceImpl")
    private final SysMenuService sysMenuService;

    @Override
    public SysMenuService getBaseRepository() {
        return sysMenuService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysMenu.class;
    }

    @Override
    public Class getTClass() {
        return SysMenu.class;
    }

    @Override
    public SysMenu findById(Long id) {
        TbSysMenu tbSysMenu = sysMenuService.getById(id);
        return MapstructUtils.convert(tbSysMenu, SysMenu.class);
    }

    @Override
    public List<SysMenu> findByIds(Collection<Long> ids) {
        List<TbSysMenu> list = sysMenuService.listByIds(ids);
        return MapstructUtils.convert(list, SysMenu.class);
    }

    @Override
    public SysMenu save(SysMenu data) {
        sysMenuService.save(MapstructUtils.convert(data, TbSysMenu.class));
        return data;
    }

    @Override
    public void batchSave(List<SysMenu> data) {
        List<TbSysMenu> tbSysMenus = data.stream().map(e -> MapstructUtils.convert(e, TbSysMenu.class)).collect(Collectors.toList());
        sysMenuService.saveBatch(tbSysMenus);
    }

    @Override
    public void deleteById(Long id) {
        sysMenuService.removeById(id);
    }

    @Override
    public void deleteByIds(Collection<Long> ids) {
        sysMenuService.removeBatchByIds(ids);
    }

    @Override
    public List<SysMenu> selectMenuList(SysMenu menu, Long userId, boolean isSuperAdmin) {

        // 管理员显示所有菜单信息
        List<TbSysMenu> tbSysMenuList;
        if (isSuperAdmin) {
            tbSysMenuList = sysMenuService.findByConditions(new LambdaQueryWrapper<TbSysMenu>()
                    .like(StringUtils.isNotBlank(menu.getMenuName()), TbSysMenu::getMenuName, menu.getMenuName())
                    .eq(StringUtils.isNotBlank(menu.getVisible()), TbSysMenu::getVisible, menu.getVisible())
                    .eq(StringUtils.isNotBlank(menu.getStatus()), TbSysMenu::getStatus, menu.getStatus())
                    .orderByAsc(TbSysMenu::getParentId)
                    .orderByAsc(TbSysMenu::getOrderNum));
        } else {
            tbSysMenuList = sysMenuService.selectMenuList(menu, userId);

        }
        return MapstructUtils.convert(tbSysMenuList, SysMenu.class);
    }

    @Override
    public List<String> selectMenuPermsByUserId(Long userId) {
//        return jpaQueryFactory.select(tbSysMenu.perms)
//                .from(tbSysMenu)
//                .leftJoin(tbSysRoleMenu).on(tbSysMenu.id.eq(tbSysRoleMenu.menuId))
//                .leftJoin(tbSysUserRole).on(tbSysRoleMenu.roleId.eq(tbSysUserRole.roleId))
//                .leftJoin(tbSysRole).on(tbSysUserRole.roleId.eq(tbSysRole.id))
//                .where(PredicateBuilder.instance()
//                        .and(tbSysMenu.status.eq("0"))
//                        .and(tbSysRole.status.eq("0"))
//                        .and(tbSysUserRole.userId.eq(userId))
//                        .build()).fetch();
        return sysMenuService.selectMenuPermsByUserId(userId);
    }

    @Override
    public List<String> selectMenuPermsByRoleId(Long roleId) {
        return sysMenuService.selectMenuPermsByRoleId(roleId);
    }

    @Override
    public List<SysMenu> selectMenuTreeAll() {
        List<TbSysMenu> rets = sysMenuService.findByConditions(new LambdaQueryWrapper<TbSysMenu>()
                .eq(TbSysMenu::getStatus, UserConstants.MENU_NORMAL)
                .in(TbSysMenu::getMenuType, Arrays.asList(UserConstants.TYPE_DIR, UserConstants.TYPE_MENU))
                .orderByAsc(TbSysMenu::getParentId)
                .orderByAsc(TbSysMenu::getOrderNum));
        return MapstructUtils.convert(rets, SysMenu.class);
    }

    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId) {
        List<TbSysMenu> sysMenuList = sysMenuService.selectMenuTreeByUserId(userId);
        return MapstructUtils.convert(sysMenuList, SysMenu.class);
    }

    @Override
    public boolean hasChildByMenuId(Long menuId) {
        TbSysMenu tbSysMenu = sysMenuService.findOneByConditions(new LambdaQueryWrapper<TbSysMenu>()
                .eq(TbSysMenu::getParentId, menuId));
        return Objects.nonNull(tbSysMenu);
    }

    @Override
    public boolean checkMenuNameUnique(SysMenu menu) {
        TbSysMenu tbSysMenu = sysMenuService.findOneByConditions(new LambdaQueryWrapper<TbSysMenu>()
                .eq(TbSysMenu::getMenuName, menu.getMenuName())
                .eq(TbSysMenu::getParentId, menu.getParentId())
                .eq(ObjectUtil.isNotNull(menu.getId()), TbSysMenu::getId, menu.getId()));
        return Objects.isNull(tbSysMenu);
    }
}
