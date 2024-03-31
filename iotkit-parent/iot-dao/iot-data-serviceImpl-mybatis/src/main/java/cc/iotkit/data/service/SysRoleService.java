package cc.iotkit.data.service;

import cc.iotkit.data.model.TbSysRole;
import cc.iotkit.model.system.SysRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysRoleService extends IService<TbSysRole> {

    List<TbSysRole> findByConditions(LambdaQueryWrapper<TbSysRole> lambdaQueryWrapper);

    TbSysRole findOneByConditions(LambdaQueryWrapper<TbSysRole> lambdaQueryWrapper);

    Page<TbSysRole> findPageByConditions(LambdaQueryWrapper<TbSysRole> lambdaQueryWrapper, int page, int size);

    List<Long> selectRoleListByUserId(Long userId);

    List<TbSysRole> selectRolePermissionByUserId(Long userId, String delFlag);

    List<TbSysRole> selectRoleList(SysRole role, String delFlag);

    List<TbSysRole> findByUserId(Long id);

}
