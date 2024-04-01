package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.constant.UserConstants;
import cc.iotkit.common.tenant.helper.TenantHelper;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StreamUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.mapper.IJPACommData;
import cc.iotkit.data.model.TbSysPost;
import cc.iotkit.data.model.TbSysRole;
import cc.iotkit.data.model.TbSysUser;
import cc.iotkit.data.system.ISysDeptData;
import cc.iotkit.data.system.ISysRoleData;
import cc.iotkit.data.system.ISysUserData;

import cc.iotkit.model.system.SysDept;
import cc.iotkit.model.system.SysRole;
import cc.iotkit.model.system.SysUser;
import cn.hutool.core.util.ObjectUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@Primary
@Service
@RequiredArgsConstructor
public class SysUserDataImpl implements ISysUserData, IJPACommData<SysUser, Long, TbSysUser> {

    @Qualifier("DBSysUserServiceImpl")
    private final SysUserService sysUserService;

    private final ISysDeptData sysDeptData;

    private final ISysRoleData sysRoleData;

    @Override
    public SysUserService getBaseRepository() {
        return sysUserService;
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
        TbSysUser tbSysUser = sysUserService.findOneByConditions(new LambdaQueryWrapper<TbSysUser>().eq(TbSysUser::getUserName, user.getUserName()).ne(Objects.nonNull(user.getId()), TbSysUser::getId, user.getId()));
        return Objects.isNull(tbSysUser);
    }

    @Override
    public boolean checkPhoneUnique(SysUser user) {
        TbSysUser tbSysUser = sysUserService.findOneByConditions(new LambdaQueryWrapper<TbSysUser>().eq(TbSysUser::getPhonenumber, user.getPhonenumber()).ne(Objects.nonNull(user.getId()), TbSysUser::getId, user.getId()));

        return Objects.isNull(tbSysUser);
    }

    @Override
    public SysUser findById(Long id) {
        TbSysUser sysUser = sysUserService.findById(id);
        SysUser convert = MapstructUtils.convert(sysUser, SysUser.class);

        if (Objects.isNull(convert)) {
            return null;
        }

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
        TbSysUser tbSysUser = sysUserService.findOneByConditions(new LambdaQueryWrapper<TbSysUser>().eq(TbSysUser::getEmail, user.getEmail()).ne(Objects.nonNull(user.getId()), TbSysUser::getId, user.getId()));
        return Objects.isNull(tbSysUser);
    }

    @Override
    public SysUser selectByPhonenumber(String phonenumber) {
        TbSysUser ret = sysUserService.findOneByConditions(new LambdaQueryWrapper<TbSysUser>().eq(TbSysUser::getPhonenumber, phonenumber));
        return MapstructUtils.convert(ret, SysUser.class);
    }

    @Override
    public SysUser selectTenantUserByPhonenumber(String phonenumber, String tenantId) {
        TbSysUser ret = sysUserService.findOneByConditions(new LambdaQueryWrapper<TbSysUser>().eq(TbSysUser::getPhonenumber, phonenumber).eq(TbSysUser::getTenantId, tenantId));

        return MapstructUtils.convert(ret, SysUser.class);
    }

    @Override
    public SysUser selectTenantUserByEmail(String email, String tenantId) {
        TbSysUser ret = sysUserService.findOneByConditions(new LambdaQueryWrapper<TbSysUser>().eq(TbSysUser::getEmail, email).eq(TbSysUser::getTenantId, tenantId));
        return MapstructUtils.convert(ret, SysUser.class);
    }

    @Override
    public SysUser selectUserByEmail(String email) {
        TbSysUser ret = sysUserService.findOneByConditions(new LambdaQueryWrapper<TbSysUser>().eq(TbSysUser::getEmail, email));
        return MapstructUtils.convert(ret, SysUser.class);
    }

    @Override
    public SysUser selectTenantUserByUserName(String username, String tenantId) {
        TbSysUser ret = sysUserService.findOneByConditions(new LambdaQueryWrapper<TbSysUser>().eq(TbSysUser::getUserName, username).eq(TbSysUser::getTenantId, tenantId));
        return MapstructUtils.convert(ret, SysUser.class);

    }

    @Override
    public SysUser selectUserByUserName(String username) {
        TbSysUser ret = sysUserService.findOneByConditions(new LambdaQueryWrapper<TbSysUser>().eq(TbSysUser::getUserName, username));
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
        Page<TbSysUser> paged = sysUserService.selectAllocatedList(to.getData(), UserConstants.ROLE_NORMAL, to.getPageNum(), to.getPageSize());
        return new Paging<>(paged.getTotal(), MapstructUtils.convert(paged.getRecords(), SysUser.class));

    }

    @Override
    public String selectUserPostGroup(String userName) {
        List<String> strings = sysUserService.selectUserPostGroup(userName);
        return String.join(",", strings);
    }

    @Override
    public String selectUserRoleGroup(String userName) {
        List<String> strings = sysUserService.selectUserRoleGroup(userName);
        return String.join(",", strings);
    }


    @Override
    public Paging<SysUser> selectUnallocatedList(PageRequest<SysUser> to) {
        Page<TbSysUser> paged = sysUserService.selectAllocatedList(to.getData(), UserConstants.ROLE_NORMAL, to.getPageNum(), to.getPageSize());
        return new Paging<>(paged.getTotal(), MapstructUtils.convert(paged.getRecords(), SysUser.class));
    }

    @Override
    public SysUser findByPhonenumber(String phonenumber) {
        TbSysUser user = sysUserService.findOneByConditions(new LambdaQueryWrapper<TbSysUser>().eq(TbSysUser::getPhonenumber, phonenumber));
        return MapstructUtils.convert(user, SysUser.class);
    }

    @Override
    public Paging<SysUser> findAll(PageRequest<SysUser> pageRequest) {
        Page<TbSysUser> paged = sysUserService.findAll(pageRequest.getPageNum(), pageRequest.getPageSize());
        return new Paging<>(paged.getTotal(), MapstructUtils.convert(paged.getRecords(), SysUser.class));
    }

    @Override
    public List<SysUser> findAllByCondition(SysUser user) {
        List<TbSysUser> sysUserList = sysUserService.listWithDeptId(user);
        return MapstructUtils.convert(sysUserList, SysUser.class);
    }

}
