package cc.iotkit.data.service;

import cc.iotkit.common.constant.UserConstants;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.SysDeptRepository;
import cc.iotkit.data.model.TbSysDept;
import cc.iotkit.data.system.ISysDeptData;
import cc.iotkit.data.util.PredicateBuilder;
import cc.iotkit.model.system.SysDept;
import cn.hutool.core.util.ObjectUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static cc.iotkit.data.model.QTbSysDept.tbSysDept;
import static cc.iotkit.data.model.QTbSysRoleDept.tbSysRoleDept;

/**
 * @Author：tfd
 * @Date：2023/5/30 13:43
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysDeptDataImpl implements ISysDeptData, IJPACommData<SysDept, Long> {

    @Autowired
    private SysDeptRepository deptRepository;

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public JpaRepository getBaseRepository() {
        return deptRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysDept.class;
    }

    @Override
    public Class getTClass() {
        return SysDept.class;
    }


    @Override
    public List<SysDept> findDepts(SysDept dept) {
        PredicateBuilder predicateBuilder = PredicateBuilder.instance()
                .and(ObjectUtil.isNotNull(dept.getId()), () -> tbSysDept.id.eq(dept.getId()))
                .and(ObjectUtil.isNotNull(dept.getParentId()), () -> tbSysDept.parentId.eq(dept.getParentId()))
                .and(StringUtils.isNotEmpty(dept.getDeptName()), () -> tbSysDept.deptName.like(dept.getDeptName()))
                .and(StringUtils.isNotEmpty(dept.getStatus()), () -> tbSysDept.status.eq(dept.getStatus()));
        return MapstructUtils.convert(StreamSupport.stream(deptRepository.findAll(predicateBuilder.build()).spliterator(), false).collect(Collectors.toList()), SysDept.class);
    }

    @Override
    public List<SysDept> findByRoleId(Long roleId) {
        List<TbSysDept> list = jpaQueryFactory.select(tbSysDept).from(tbSysDept).leftJoin(tbSysRoleDept).on(tbSysDept.id.eq(tbSysRoleDept.deptId))
                .where(tbSysRoleDept.roleId.eq(roleId)).orderBy(tbSysDept.parentId.desc(), tbSysDept.orderNum.desc()).fetch();
        return MapstructUtils.convert(list, SysDept.class);

    }

    @Override
    public long countByParentId(Long parentId) {
        return jpaQueryFactory.select(tbSysDept.id.count()).from(tbSysDept).where(tbSysDept.parentId.eq(parentId)).fetchOne();
    }

    @Override
    public List<SysDept> findByDeptId(Long deptId) {
        return MapstructUtils.convert(deptRepository.findAll().stream().filter(o -> o.getAncestors() != null && o.getAncestors().contains(deptId.toString()))
                .collect(Collectors.toList()), SysDept.class);
    }

    @Override
    public boolean checkDeptNameUnique(String deptName, Long parentId, Long deptId) {
        PredicateBuilder predicateBuilder = PredicateBuilder.instance().and(tbSysDept.deptName.eq(deptName))
                .and(tbSysDept.parentId.eq(parentId));
        if (ObjectUtil.isNotNull(deptId)) {
            predicateBuilder.and(tbSysDept.id.ne(deptId));
        }
        Long count = jpaQueryFactory.select(tbSysDept.id.count())
                .from(tbSysDept)
                .where(predicateBuilder.build())
                .fetchOne();
        return count == 0;
    }

    @Override
    public long selectNormalChildrenDeptById(Long deptId) {

        PredicateBuilder predicateBuilder = PredicateBuilder.instance().and(tbSysDept.status.eq(UserConstants.DEPT_NORMAL));
        return jpaQueryFactory.select(tbSysDept.ancestors).where(predicateBuilder.build()).fetch().stream().filter(o -> o.indexOf(deptId.toString()) != -1).count();


    }
}
