package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.constant.UserConstants;
import cc.iotkit.common.tenant.helper.TenantHelper;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StreamUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.SysUserRepository;
import cc.iotkit.data.model.TbSysPost;
import cc.iotkit.data.model.TbSysRole;
import cc.iotkit.data.model.TbSysUser;
import cc.iotkit.data.system.ISysDeptData;
import cc.iotkit.data.system.ISysRoleData;
import cc.iotkit.data.system.ISysUserData;
import cc.iotkit.data.util.PageBuilder;
import cc.iotkit.data.util.PredicateBuilder;
import cc.iotkit.model.system.SysDept;
import cc.iotkit.model.system.SysRole;
import cc.iotkit.model.system.SysUser;
import cn.hutool.core.util.ObjectUtil;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static cc.iotkit.data.model.QTbSysDept.tbSysDept;
import static cc.iotkit.data.model.QTbSysPost.tbSysPost;
import static cc.iotkit.data.model.QTbSysRole.tbSysRole;
import static cc.iotkit.data.model.QTbSysUser.tbSysUser;
import static cc.iotkit.data.model.QTbSysUserPost.tbSysUserPost;
import static cc.iotkit.data.model.QTbSysUserRole.tbSysUserRole;


/**
 * @Author：tfd
 * @Date：2023/5/29 16:00
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysUserDataImpl implements ISysUserData, IJPACommData<SysUser, Long> {

    private final SysUserRepository userRepository;

    private final ISysDeptData sysDeptData;

    private final ISysRoleData sysRoleData;

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public JpaRepository getBaseRepository() {
        return userRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysUser.class;
    }

    @Override
    public Class getTClass() {
        return SysUser.class;
    }

    @Override
    public long countByDeptId(Long aLong) {
        return 0;
    }

    @Override
    public boolean checkUserNameUnique(SysUser user) {
        final TbSysUser ret = jpaQueryFactory.select(tbSysUser).from(tbSysUser)
                .where(PredicateBuilder.instance()
                        .and(tbSysUser.userName.eq(user.getUserName()))
                        .and(Objects.nonNull(user.getId()), () -> tbSysUser.id.ne(user.getId()))
                        .build()).fetchOne();
        return Objects.isNull(ret);
    }

    @Override
    public boolean checkPhoneUnique(SysUser user) {
        final TbSysUser ret = jpaQueryFactory.select(tbSysUser).from(tbSysUser)
                .where(PredicateBuilder.instance()
                        .and(tbSysUser.phonenumber.eq(user.getPhonenumber()))
                        .and(Objects.nonNull(user.getId()), () -> tbSysUser.id.ne(user.getId()))
                        .build()).fetchOne();
        return Objects.isNull(ret);
    }

    @Override
    public SysUser findById(Long id) {
        Optional<TbSysUser> optUser = userRepository.findById(id);
        if (optUser.isEmpty()) {
            return null;
        }

        SysUser convert = MapstructUtils.convert(optUser.get(), SysUser.class);
        List<SysRole> sysRoles = sysRoleData.findByUserId(id);
        convert.setRoles(sysRoles);

        Long deptId = convert.getDeptId();
        if (deptId == null) {
            return convert;
        }

        SysDept dept = sysDeptData.findById(deptId);
        if (ObjectUtil.isNotNull(dept)) {
            convert.setDept(dept);
        }
        return convert;
    }

    @Override
    public boolean checkEmailUnique(SysUser user) {
        final TbSysUser ret = jpaQueryFactory.select(tbSysUser).from(tbSysUser)
                .where(PredicateBuilder.instance()
                        .and(tbSysUser.email.eq(user.getEmail()))
                        .and(Objects.nonNull(user.getId()), () -> tbSysUser.id.ne(user.getId()))
                        .build()).fetchOne();
        return Objects.isNull(ret);
    }

    @Override
    public SysUser selectByPhonenumber(String phonenumber) {
        TbSysUser ret = jpaQueryFactory.select(tbSysUser).from(tbSysUser)
                .where(PredicateBuilder.instance()
                        .and(tbSysUser.phonenumber.eq(phonenumber))
                        .build()).fetchOne();
        return MapstructUtils.convert(ret, SysUser.class);
    }

    @Override
    public SysUser selectTenantUserByPhonenumber(String phonenumber, String tenantId) {
        TbSysUser ret = jpaQueryFactory.select(tbSysUser).from(tbSysUser)
                .where(PredicateBuilder.instance()
                        .and(tbSysUser.phonenumber.eq(phonenumber))
                        .and(tbSysUser.tenantId.eq(tenantId))
                        .build()).fetchOne();
        return MapstructUtils.convert(ret, SysUser.class);
    }

    @Override
    public SysUser selectTenantUserByEmail(String email, String tenantId) {
        TbSysUser ret = jpaQueryFactory.select(tbSysUser).from(tbSysUser)
                .where(PredicateBuilder.instance()
                        .and(tbSysUser.email.eq(email))
                        .and(tbSysUser.tenantId.eq(tenantId))
                        .build()).fetchOne();
        return MapstructUtils.convert(ret, SysUser.class);
    }

    @Override
    public SysUser selectUserByEmail(String email) {
        TbSysUser ret = jpaQueryFactory.select(tbSysUser).from(tbSysUser)
                .where(PredicateBuilder.instance()
                        .and(tbSysUser.email.eq(email))
                        .build()).fetchOne();
        return MapstructUtils.convert(ret, SysUser.class);
    }

    @Override
    public SysUser selectTenantUserByUserName(String username, String tenantId) {
        TbSysUser ret = jpaQueryFactory.select(tbSysUser).from(tbSysUser)
                .where(PredicateBuilder.instance()
                        .and(tbSysUser.userName.eq(username))
                        .and(TenantHelper.isEnable(), () -> tbSysUser.tenantId.eq(tenantId))
                        .build()).fetchOne();
        if (Objects.nonNull(ret)) {
            SysUser convert = MapstructUtils.convert(ret, SysUser.class);
            Long deptId = ret.getDeptId();
            if (Objects.nonNull(deptId)) {
                // 获取部门信息
                SysDept sysDept = sysDeptData.findById(deptId);
                convert.setDept(sysDept);
                // 获取角色信息
                List<SysRole> sysRoles = sysRoleData.findByUserId(ret.getId());

                convert.setRoles(sysRoles);
            }
            return MapstructUtils.convert(ret, SysUser.class);
        } else {
            return null;
        }

    }

    @Override
    public SysUser selectUserByUserName(String username) {
        TbSysUser ret = jpaQueryFactory.select(tbSysUser).from(tbSysUser)
                .where(PredicateBuilder.instance()
                        .and(tbSysUser.userName.eq(username))
                        .build()).fetchOne();
        SysUser convert = MapstructUtils.convert(ret, SysUser.class);
        Long deptId = ret.getDeptId();
        if (Objects.nonNull(deptId)) {
            // 获取部门信息
            SysDept sysDept = sysDeptData.findById(deptId);
            convert.setDept(sysDept);
            // 获取角色信息
            List<SysRole> sysRoles = sysRoleData.findByUserId(ret.getId());

            convert.setRoles(sysRoles);
        }
        return convert;
    }

    @Override
    public Paging<SysUser> selectAllocatedList(PageRequest<SysUser> to) {
        SysUser data = to.getData();

        PredicateBuilder builder = PredicateBuilder.instance()
                .and(StringUtils.isNotBlank(data.getPhonenumber()), () -> tbSysUser.phonenumber.like(data.getPhonenumber()))
                .and(StringUtils.isNotBlank(data.getUserName()), () -> tbSysUser.userName.like(data.getUserName()))
                .and(StringUtils.isNotBlank(data.getStatus()), () -> tbSysUser.status.eq(data.getStatus()))
                .and(Objects.nonNull(data.getDeptId()), () -> tbSysUser.deptId.eq(data.getDeptId()))
                .and(tbSysUser.delFlag.eq(UserConstants.ROLE_NORMAL))
                .and(Objects.nonNull(data.getRoleId()), () -> tbSysRole.id.eq(data.getRoleId()));

        QueryResults<TbSysUser> tbSysUserQueryResults = jpaQueryFactory.select(Projections.bean(TbSysUser.class, tbSysUser.id, tbSysUser.deptId, tbSysUser.userName,
                tbSysUser.nickName, tbSysUser.email, tbSysUser.phonenumber, tbSysUser.createTime)).from(tbSysUser)
                .leftJoin(tbSysDept).on(tbSysUser.deptId.eq(tbSysDept.id))
                .leftJoin(tbSysUserRole).on(tbSysUser.id.eq(tbSysUserRole.userId))
                .leftJoin(tbSysRole).on(tbSysUserRole.roleId.eq(tbSysRole.id))
                .where(builder.build()).offset(to.getOffset()).limit(to.getPageSize()).fetchResults();
        return new Paging<>(tbSysUserQueryResults.getTotal(), MapstructUtils.convert(tbSysUserQueryResults.getResults(), SysUser.class));

    }

    @Override
    public String selectUserPostGroup(String userName) {
        List<TbSysPost> fetch = jpaQueryFactory.select(Projections.bean(TbSysPost.class, tbSysPost.id, tbSysPost.postName, tbSysPost.postCode)).from(tbSysPost)
                .leftJoin(tbSysUserPost).on(tbSysPost.id.eq(tbSysUserPost.postId))
                .leftJoin(tbSysUser).on(tbSysUserPost.userId.eq(tbSysUser.id))
                .where(tbSysUser.userName.eq(userName))
                .orderBy(tbSysPost.postSort.asc()).fetch();
        return fetch.stream().map(TbSysPost::getPostName).collect(Collectors.joining(","));
    }

    @Override
    public String selectUserRoleGroup(String userName) {
        List<TbSysRole> fetch = jpaQueryFactory.select(tbSysRole).from(tbSysRole)
                .leftJoin(tbSysUserRole).on(tbSysRole.id.eq(tbSysUserRole.roleId))
                .leftJoin(tbSysUser).on(tbSysUserRole.userId.eq(tbSysUser.id))
                .where(tbSysUser.userName.eq(userName))
                .orderBy(tbSysRole.roleSort.asc()).fetch();
        return fetch.stream().map(TbSysRole::getRoleName).collect(Collectors.joining(","));
    }


    @Override
    public Paging<SysUser> selectUnallocatedList(PageRequest<SysUser> to) {
        SysUser data = to.getData();
        PredicateBuilder builder = PredicateBuilder.instance();
        if (Objects.nonNull(data)) {
            builder.and(StringUtils.isNotBlank(data.getPhonenumber()), () -> tbSysUser.phonenumber.like(data.getPhonenumber()))
                    .and(StringUtils.isNotBlank(data.getUserName()), () -> tbSysUser.userName.like(data.getUserName()))
                    .and(StringUtils.isNotBlank(data.getStatus()), () -> tbSysUser.status.eq(data.getStatus()))
                    .and(Objects.nonNull(data.getDeptId()), () -> tbSysUser.deptId.eq(data.getDeptId()))
                    .and(tbSysUser.delFlag.eq(UserConstants.ROLE_NORMAL));
        }

        QueryResults<SysUser> sysUserQueryResults = jpaQueryFactory.select(Projections.bean(SysUser.class, tbSysUser.id, tbSysUser.deptId, tbSysUser.userName,
                tbSysUser.nickName, tbSysUser.email, tbSysUser.phonenumber, tbSysUser.createTime)).from(tbSysUser)
                .leftJoin(tbSysDept).on(tbSysUser.deptId.eq(tbSysDept.id))
                .leftJoin(tbSysUserRole).on(tbSysUser.id.eq(tbSysUserRole.userId))
                .leftJoin(tbSysRole).on(tbSysUserRole.roleId.eq(tbSysRole.id))
                .where(builder.build()).offset(to.getOffset()).limit(to.getPageSize()).fetchResults();
        return new Paging<>(sysUserQueryResults.getTotal(), sysUserQueryResults.getResults());
    }

    @Override
    public SysUser findByPhonenumber(String phonenumber) {
        TbSysUser user = jpaQueryFactory.select(tbSysUser).from(tbSysUser)
                .where(PredicateBuilder.instance()
                        .and(tbSysUser.phonenumber.eq(phonenumber))
                        .build()).fetchOne();
        return MapstructUtils.convert(user, SysUser.class);
    }

    @Override
    public Paging<SysUser> findAll(PageRequest<SysUser> pageRequest) {
        return PageBuilder.toPaging(userRepository.findAll(buildQueryCondition(pageRequest.getData()), PageBuilder.toPageable(pageRequest))).to(SysUser.class);
    }

    @Override
    public List<SysUser> findAllByCondition(SysUser user) {
        return buildQuery(buildQueryCondition(user));
    }

    private List<SysUser> buildQuery(Predicate predicate) {
        List<TbSysUser> users = jpaQueryFactory.select(Projections.bean(TbSysUser.class, tbSysUser.id, tbSysUser.deptId,
                tbSysUser.nickName, tbSysUser.userName, tbSysUser.email, tbSysUser.avatar, tbSysUser.phonenumber, tbSysUser.sex,
                tbSysUser.status, tbSysUser.delFlag, tbSysUser.loginIp, tbSysUser.loginDate, tbSysUser.createBy, tbSysUser.createTime,
                tbSysUser.remark, tbSysDept.deptName, tbSysDept.leader))
                .from(tbSysUser)
                .leftJoin(tbSysDept).on(tbSysUser.deptId.eq(tbSysDept.id))
                .where(predicate).fetch();
        return MapstructUtils.convert(users, SysUser.class);
    }

    private Predicate buildQueryCondition(SysUser user) {
        List<Long> ids;
        if (Objects.nonNull(user) && Objects.nonNull(user.getDeptId())) {
            Long deptId = user.getDeptId();
            List<SysDept> depts = sysDeptData.findByDeptId(deptId);
            ids = StreamUtils.toList(depts, SysDept::getId);
            ids.add(deptId);
        } else {
            ids = null;
        }
        return PredicateBuilder.instance()
                .and(ObjectUtil.isNotNull(user.getId()), () -> tbSysUser.id.eq(user.getId()))
                .and(StringUtils.isNotEmpty(user.getUserName()), () -> tbSysUser.userName.like("%" + user.getUserName() + "%"))
                .and(StringUtils.isNotEmpty(user.getStatus()), () -> tbSysUser.status.eq(user.getStatus()))
                .and(StringUtils.isNotEmpty(user.getPhonenumber()), () -> tbSysUser.phonenumber.like("%" + user.getPhonenumber() + "%"))
                .and(ObjectUtil.isNotEmpty(ids), () -> tbSysUser.deptId.in(ids)).build();
    }
}
