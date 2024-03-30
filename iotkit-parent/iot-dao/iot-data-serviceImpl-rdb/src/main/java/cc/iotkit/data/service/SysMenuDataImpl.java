package cc.iotkit.data.service;

import cc.iotkit.common.constant.UserConstants;
import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.SysMenuRepository;
import cc.iotkit.data.model.QTbSysMenu;
import cc.iotkit.data.model.TbSysMenu;
import cc.iotkit.data.system.ISysMenuData;
import cc.iotkit.data.util.PredicateBuilder;
import cc.iotkit.model.system.SysMenu;
import cn.hutool.core.util.ObjectUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cc.iotkit.data.model.QTbSysMenu.tbSysMenu;
import static cc.iotkit.data.model.QTbSysRole.tbSysRole;
import static cc.iotkit.data.model.QTbSysRoleMenu.tbSysRoleMenu;
import static cc.iotkit.data.model.QTbSysUser.tbSysUser;
import static cc.iotkit.data.model.QTbSysUserRole.tbSysUserRole;


/**
 * @Author: 石恒
 * @Date: 2023/5/28 15:43
 * @Description:
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysMenuDataImpl implements ISysMenuData, IJPACommData<SysMenu, Long> {

    private final SysMenuRepository sysMenuRepository;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public JpaRepository getBaseRepository() {
        return sysMenuRepository;
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
        TbSysMenu tbSysMenu = sysMenuRepository.findById(id).orElseThrow(() ->
                new BizException(ErrCode.DATA_NOT_EXIST));
        return MapstructUtils.convert(tbSysMenu, SysMenu.class);
    }

    @Override
    public List<SysMenu> findByIds(Collection<Long> ids) {
        List<TbSysMenu> list = sysMenuRepository.findAllById(ids);
        return MapstructUtils.convert(list, SysMenu.class);
    }

    @Override
    public SysMenu save(SysMenu data) {
        sysMenuRepository.save(MapstructUtils.convert(data, TbSysMenu.class));
        return data;
    }

    @Override
    public void batchSave(List<SysMenu> data) {
        List<TbSysMenu> tbSysMenus = data.stream().map(e -> MapstructUtils.convert(e, TbSysMenu.class)).collect(Collectors.toList());
        sysMenuRepository.saveAll(tbSysMenus);
    }

    @Override
    public void deleteById(Long id) {
        sysMenuRepository.deleteById(id);
    }

    @Override
    public void deleteByIds(Collection<Long> ids) {
        sysMenuRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    public List<SysMenu> selectMenuList(SysMenu menu, Long userId, boolean isSuperAdmin) {

        PredicateBuilder predicateBuilder = PredicateBuilder.instance()
                .and(StringUtils.isNotBlank(menu.getMenuName()), () -> tbSysMenu.menuName.like(menu.getMenuName()))
                .and(StringUtils.isNotBlank(menu.getVisible()), () -> tbSysMenu.visible.eq(menu.getVisible()))
                .and(StringUtils.isNotBlank(menu.getStatus()), () -> tbSysMenu.status.eq(menu.getStatus()));

        // 管理员显示所有菜单信息
        List<TbSysMenu> tbSysMenuList;
        if (isSuperAdmin) {
            tbSysMenuList = jpaQueryFactory.select(tbSysMenu)
                    .from(tbSysMenu)
                    .where(predicateBuilder.build())
                    .orderBy(tbSysMenu.parentId.asc(), tbSysMenu.orderNum.asc()).fetch();
        } else {
            tbSysMenuList = jpaQueryFactory.select(Projections.bean(TbSysMenu.class, tbSysMenu.id,
                            tbSysMenu.parentId, tbSysMenu.menuName, tbSysMenu.path, tbSysMenu.component, tbSysMenu.queryParam,
                            tbSysMenu.visible, tbSysMenu.status, tbSysMenu.perms, tbSysMenu.isFrame, tbSysMenu.isCache, tbSysMenu.menuType,
                            tbSysMenu.icon, tbSysMenu.orderNum, tbSysMenu.createTime))
                    .from(tbSysMenu)
                    .leftJoin(tbSysRoleMenu).on(tbSysMenu.id.eq(tbSysRoleMenu.menuId))
                    .leftJoin(tbSysUserRole).on(tbSysRoleMenu.roleId.eq(tbSysUserRole.roleId))
                    .leftJoin(tbSysRole).on(tbSysUserRole.roleId.eq(tbSysRole.id))
                    .where(predicateBuilder
                            .and(tbSysUserRole.userId.eq(userId))
                            .build())
                    .orderBy(tbSysMenu.parentId.asc(), tbSysMenu.orderNum.asc()).fetch();

        }
        return MapstructUtils.convert(tbSysMenuList, SysMenu.class);

    }

    @Override
    public List<String> selectMenuPermsByUserId(Long userId) {
        return jpaQueryFactory.select(tbSysMenu.perms)
                .from(tbSysMenu)
                .leftJoin(tbSysRoleMenu).on(tbSysMenu.id.eq(tbSysRoleMenu.menuId))
                .leftJoin(tbSysUserRole).on(tbSysRoleMenu.roleId.eq(tbSysUserRole.roleId))
                .leftJoin(tbSysRole).on(tbSysUserRole.roleId.eq(tbSysRole.id))
                .where(PredicateBuilder.instance()
                        .and(tbSysMenu.status.eq("0"))
                        .and(tbSysRole.status.eq("0"))
                        .and(tbSysUserRole.userId.eq(userId))
                        .build()).fetch();
    }

    @Override
    public List<String> selectMenuPermsByRoleId(Long roleId) {
        return jpaQueryFactory.select(Projections.bean(String.class, tbSysMenu.perms.countDistinct()))
                .from(tbSysMenu)
                .leftJoin(tbSysRoleMenu).on(tbSysMenu.id.eq(tbSysRoleMenu.menuId))
                .where(PredicateBuilder.instance()
                        .and(tbSysMenu.status.eq("0"))
                        .and(tbSysRoleMenu.roleId.eq(roleId))
                        .build()).fetch();
    }

    @Override
    public List<SysMenu> selectMenuTreeAll() {
         List<TbSysMenu> rets= jpaQueryFactory.select(tbSysMenu)
                .from(tbSysMenu)
                .where(PredicateBuilder.instance()
                        .and(tbSysMenu.menuType.in(UserConstants.TYPE_DIR, UserConstants.TYPE_MENU))
                        .and(tbSysMenu.status.eq(UserConstants.MENU_NORMAL))
                        .build())
                .orderBy(tbSysMenu.parentId.asc(), tbSysMenu.orderNum.asc()).fetch();
        return MapstructUtils.convert(rets,SysMenu.class);
    }

    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId) {
        return jpaQueryFactory.select(Projections.bean(SysMenu.class, tbSysMenu.id.as(tbSysMenu.id),
                        tbSysMenu.parentId, tbSysMenu.menuName, tbSysMenu.path, tbSysMenu.component, tbSysMenu.queryParam,
                        tbSysMenu.visible, tbSysMenu.status, tbSysMenu.perms, tbSysMenu.isFrame, tbSysMenu.isCache, tbSysMenu.menuType,
                        tbSysMenu.icon, tbSysMenu.orderNum, tbSysMenu.createTime))
                .distinct()
                .from(tbSysMenu)
                .leftJoin(tbSysRoleMenu).on(tbSysMenu.id.eq(tbSysRoleMenu.menuId))
                .leftJoin(tbSysUserRole).on(tbSysRoleMenu.roleId.eq(tbSysUserRole.roleId))
                .leftJoin(tbSysRole).on(tbSysUserRole.roleId.eq(tbSysRole.id))
                .leftJoin(tbSysUser).on(tbSysUserRole.userId.eq(tbSysUser.id))
                .where(PredicateBuilder.instance()
                        .and(tbSysUser.id.eq(userId))
                        .and(tbSysMenu.menuType.in("M", "C"))
                        .and(tbSysMenu.status.eq("0"))
                        .and(tbSysRole.status.eq("0"))
                        .build())
                .orderBy(tbSysMenu.parentId.asc(), tbSysMenu.orderNum.asc()).fetch();
    }

    @Override
    public boolean hasChildByMenuId(Long menuId) {
        TbSysMenu tbSysMenu = jpaQueryFactory.select(QTbSysMenu.tbSysMenu).from(QTbSysMenu.tbSysMenu)
                .where(QTbSysMenu.tbSysMenu.parentId.eq(menuId)).limit(1).fetchOne();
        return Objects.nonNull(tbSysMenu);
    }

    @Override
    public boolean checkMenuNameUnique(SysMenu menu) {
        TbSysMenu tbSysMenu = jpaQueryFactory.select(QTbSysMenu.tbSysMenu).from(QTbSysMenu.tbSysMenu)
                .where(
                        PredicateBuilder.instance()
                                .and(QTbSysMenu.tbSysMenu.menuName.eq(menu.getMenuName()))
                                .and(QTbSysMenu.tbSysMenu.parentId.eq(menu.getParentId()))
                                .and(ObjectUtil.isNotNull(menu.getId()), () -> QTbSysMenu.tbSysMenu.id.ne(menu.getId()))
                                .build()).fetchOne();
        return Objects.isNull(tbSysMenu);
    }
}
