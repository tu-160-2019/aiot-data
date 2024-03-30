package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.SysRoleMenuRepository;
import cc.iotkit.data.model.QTbSysRoleMenu;
import cc.iotkit.data.model.TbSysRoleMenu;
import cc.iotkit.data.system.ISysRoleMenuData;
import cc.iotkit.data.util.PredicateBuilder;
import cc.iotkit.model.system.SysRoleMenu;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static cc.iotkit.data.model.QTbSysRoleMenu.tbSysRoleMenu;

/**
 * author: 石恒
 * date: 2023-05-30 11:00
 * description:
 **/
@Primary
@Service
@RequiredArgsConstructor
public class SysRoleMenuDataImpl implements ISysRoleMenuData, IJPACommData<SysRoleMenu, Long> {


    private final SysRoleMenuRepository sysRoleMenuRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public JpaRepository getBaseRepository() {
        return sysRoleMenuRepository;
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
        return jpaQueryFactory
                .select(QTbSysRoleMenu.tbSysRoleMenu.count())
                .from(QTbSysRoleMenu.tbSysRoleMenu)
                .where(PredicateBuilder.instance()
                        .and(QTbSysRoleMenu.tbSysRoleMenu.menuId.eq(menuId))
                        .build()).fetchOne() > 0;
    }

    @Override
    public long insertBatch(List<SysRoleMenu> list) {
        List<TbSysRoleMenu> tbSysRoleMenus = Objects.requireNonNull(MapstructUtils.convert(list, TbSysRoleMenu.class));
        return sysRoleMenuRepository.saveAll(tbSysRoleMenus).size();
    }

    @Override
    public long deleteByRoleId(Collection<Long> ids) {
        return jpaQueryFactory.delete(tbSysRoleMenu).where(tbSysRoleMenu.roleId.in(ids)).execute();
    }


}
