package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.model.TbSysUserRole;
import cc.iotkit.data.system.ISysUserRoleData;
import cc.iotkit.data.util.PredicateBuilder;
import cc.iotkit.model.system.SysUserRole;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;

import org.springframework.stereotype.Service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author：tfd
 * @Date：2023/5/30 16:36
 */
@Primary
@Service
@RequiredArgsConstructor
public class SysUserRoleDataImpl implements ISysUserRoleData, IJPACommData<SysUserRole, Long, TbSysUserRole> {

    private final SysUserRoleService sysUserRoleService;

    @Override
    public SysUserRoleService getBaseRepository() {
        return sysUserRoleService;
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
        boolean res = sysUserRoleService.remove(new LambdaQueryWrapper<TbSysUserRole>()
                .eq(TbSysUserRole::getUserId, userId));
        return BooleanUtils.toInteger(res);
    }

    @Override
    public long countUserRoleByRoleId(Long roleId) {
        return sysUserRoleService.count(new LambdaQueryWrapper<TbSysUserRole>().eq(TbSysUserRole::getRoleId, roleId));
    }

    @Override
    public long insertBatch(List<SysUserRole> list) {
        boolean res = sysUserRoleService.saveBatch(MapstructUtils.convert(list, TbSysUserRole.class), list.size());
        return res ? list.size() : 0L;
    }

    @Override
    public long delete(Long roleId, List<Long> userIds) {
        boolean res = sysUserRoleService.remove(new LambdaQueryWrapper<TbSysUserRole>()
                .eq(TbSysUserRole::getRoleId, roleId).in(TbSysUserRole::getUserId, userIds));
        return res ? userIds.size() : 0L;
    }

}
