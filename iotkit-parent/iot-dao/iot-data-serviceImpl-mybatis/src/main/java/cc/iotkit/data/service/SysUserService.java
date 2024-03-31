package cc.iotkit.data.service;

import cc.iotkit.data.model.TbSysUser;
import cc.iotkit.model.system.SysUser;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;


public interface SysUserService extends IService<TbSysUser> {

    List<TbSysUser> findByConditions(LambdaQueryWrapper<TbSysUser> lambdaQueryWrapper);

    TbSysUser findOneByConditions(LambdaQueryWrapper<TbSysUser> lambdaQueryWrapper);

    TbSysUser findById(Long id);

    Page<TbSysUser> findPageByConditions(LambdaQueryWrapper<TbSysUser> lambdaQueryWrapper, int page, int size);

    Page<TbSysUser> findAll(int page, int size);

    List<String> selectUserPostGroup(String userName);

    List<String> selectUserRoleGroup(String userName);

    Page<TbSysUser> selectAllocatedList(SysUser sysUser, String delFlag, Integer page, Integer size);

    List<TbSysUser> listWithDeptId(SysUser user);

}
