package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.SysUserRoleRepository;
import cc.iotkit.data.model.TbSysUserRole;
import cc.iotkit.data.system.ISysUserRoleData;
import cc.iotkit.data.util.PredicateBuilder;
import cc.iotkit.model.system.SysUserRole;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

import static cc.iotkit.data.model.QTbSysUserRole.tbSysUserRole;

/**
 * @Author：tfd
 * @Date：2023/5/30 16:36
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysUserRoleDataImpl implements ISysUserRoleData, IJPACommData<SysUserRole, Long> {

    private final SysUserRoleRepository sysUserRoleRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public JpaRepository getBaseRepository() {
        return sysUserRoleRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysUserRole.class;
    }

    @Override
    public Class getTClass() {
        return SysUserRole.class;
    }

    @Override
    public int deleteByUserId(Long userId) {
        return sysUserRoleRepository.deleteAllByUserId(userId);
    }

    @Override
    public long countUserRoleByRoleId(Long roleId) {
        return sysUserRoleRepository.count(tbSysUserRole.roleId.eq(roleId));
    }

    @Override
    public long insertBatch(List<SysUserRole> list) {
        return sysUserRoleRepository.saveAll(MapstructUtils.convert(list,TbSysUserRole.class)).size();
    }

    @Override
    public long delete(Long roleId, List<Long> userIds) {
        return jpaQueryFactory.delete(tbSysUserRole).where(PredicateBuilder.instance()
                .and(tbSysUserRole.roleId.eq(roleId))
                .and(tbSysUserRole.userId.in(userIds))
                .build()).execute();
    }

}
