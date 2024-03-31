package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.SysRoleMapper;
import cc.iotkit.data.model.TbSysRole;
import cc.iotkit.data.service.SysRoleService;
import cc.iotkit.model.system.SysRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("DBSysRoleServiceImpl")
@Primary
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, TbSysRole> implements SysRoleService {


    @Override
    public List<TbSysRole> findByConditions(LambdaQueryWrapper<TbSysRole> lambdaQueryWrapper) {
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public TbSysRole findOneByConditions(LambdaQueryWrapper<TbSysRole> lambdaQueryWrapper) {
        return this.baseMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public Page<TbSysRole> findPageByConditions(LambdaQueryWrapper<TbSysRole> lambdaQueryWrapper, int page, int size) {
        Page<TbSysRole> rowPage = new Page<>(page, size);
        return this.baseMapper.selectPage(rowPage, lambdaQueryWrapper);
    }

    @Override
    public List<Long> selectRoleListByUserId(Long userId) {
        return this.baseMapper.selectRoleListByUserId(userId);
    }

    @Override
    public List<TbSysRole> selectRolePermissionByUserId(Long userId, String delFlag) {
        return this.baseMapper.selectRolePermissionByUserId(userId, delFlag);
    }

    @Override
    public List<TbSysRole> selectRoleList(SysRole role, String delFlag) {
        return this.baseMapper.selectRoleList(role,delFlag);
    }

    @Override
    public List<TbSysRole> findByUserId(Long id) {
        return this.baseMapper.findByUserId(id);
    }
}
