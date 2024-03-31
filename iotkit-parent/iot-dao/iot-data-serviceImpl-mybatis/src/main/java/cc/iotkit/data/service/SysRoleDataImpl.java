package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.constant.UserConstants;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.model.TbSysRole;
import cc.iotkit.data.system.ISysRoleData;
import cc.iotkit.data.util.PageBuilder;
import cc.iotkit.data.util.PredicateBuilder;
import cc.iotkit.model.system.SysRole;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;


/**
 * author: 石恒
 * date: 2023-05-29 16:23
 * description:
 **/
@Primary
@Service
@RequiredArgsConstructor
public class SysRoleDataImpl implements ISysRoleData, IJPACommData<SysRole, Long, TbSysRole> {

    @Qualifier("DBSysRoleServiceImpl")
    private final SysRoleService sysRoleService;

    @Qualifier("DBSysMenuServiceImpl")
    private final SysMenuService sysMenuService;

    @Override
    public SysRoleService getBaseRepository() {
        return sysRoleService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysRole.class;
    }

    @Override
    public Class getTClass() {
        return SysRole.class;
    }

    @Override
    public SysRole findById(Long id) {
        return MapstructUtils.convert(sysRoleService.getById(id), SysRole.class);
    }

    @Override
    public List<Long> selectMenuListByRoleId(Long roleId, boolean menuCheckStrictly) {
        List<Long> roleIds = null;
        if (menuCheckStrictly) {
            roleIds = sysMenuService.listParentIdByRoleId(roleId);
        }
        return sysMenuService.selectMenuListByRoleId(roleIds, menuCheckStrictly, roleId);
    }

    @Override
    public List<SysRole> selectRolePermissionByUserId(Long userId) {
        List<TbSysRole> sysRoleList = sysRoleService.selectRolePermissionByUserId(userId, UserConstants.NORMAL);
        return MapstructUtils.convert(sysRoleList, SysRole.class);
    }

    @Override
    public List<Long> selectRoleListByUserId(Long userId) {
        return sysRoleService.selectRoleListByUserId(userId);

    }

    @Override
    public boolean checkRoleNameUnique(SysRole role) {
        TbSysRole tbSysRoleRes = sysRoleService.findOneByConditions(new LambdaQueryWrapper<TbSysRole>()
                .eq(TbSysRole::getRoleName, role.getRoleName())
                .eq(Objects.nonNull(role.getId()), TbSysRole::getId, role.getId()));

        return Objects.isNull(tbSysRoleRes);
    }

    @Override
    public boolean checkRoleKeyUnique(SysRole role) {
        TbSysRole tbSysRoleRes = sysRoleService.findOneByConditions(new LambdaQueryWrapper<TbSysRole>()
                .eq(TbSysRole::getRoleKey, role.getRoleKey())
                .eq(Objects.nonNull(role.getId()), TbSysRole::getId, role.getId()));
        return Objects.isNull(tbSysRoleRes);
    }

    @Override
    public int updateById(SysRole role) {
        TbSysRole tbSysRole = MapstructUtils.convert(role, TbSysRole.class);
        boolean res = sysRoleService.updateById(tbSysRole);
        return BooleanUtils.toInteger(res);
    }

    @Override
    public Paging<SysRole> findAll(PageRequest<SysRole> pageRequest) {
        SysRole sysRole = pageRequest.getData();
        LambdaQueryWrapper<TbSysRole> lambdaQueryWrapper = new LambdaQueryWrapper<TbSysRole>()
                .eq(TbSysRole::getDelFlag, UserConstants.ROLE_NORMAL)
                .eq(Objects.nonNull(sysRole.getId()), TbSysRole::getId, sysRole.getId())
                .like(StringUtils.isNotBlank(sysRole.getRoleName()), TbSysRole::getRoleName, sysRole.getRoleName())
                .eq(StringUtils.isNotBlank(sysRole.getStatus()), TbSysRole::getStatus, sysRole.getStatus())
                .like(StringUtils.isNotBlank(sysRole.getRoleKey()), TbSysRole::getRoleKey, sysRole.getRoleKey());
        Page<TbSysRole> paged = sysRoleService.findPageByConditions(lambdaQueryWrapper, pageRequest.getPageNum(), pageRequest.getPageSize());
        return new Paging<>(paged.getTotal(), MapstructUtils.convert(paged.getRecords(), SysRole.class));
    }

    @Override
    public List<SysRole> selectRoleList(SysRole role) {
        List<TbSysRole> sysRoleList = sysRoleService.selectRoleList(role, UserConstants.ROLE_NORMAL);
        return MapstructUtils.convert(sysRoleList, SysRole.class);
    }

    @Override
    public List<SysRole> findByUserId(Long id) {
        List<TbSysRole> sysRoleList = sysRoleService.findByUserId(id);
        return MapstructUtils.convert(sysRoleList, SysRole.class);
    }

    @Override
    public void deleteById(Long id) {
        sysRoleService.removeById(id);
    }
}
