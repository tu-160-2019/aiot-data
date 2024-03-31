package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.SysUserMapper;
import cc.iotkit.data.model.TbSysUser;
import cc.iotkit.data.service.SysUserService;
import cc.iotkit.model.system.SysUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("DBSysUserServiceImpl")
@Primary
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, TbSysUser> implements SysUserService {

    @Override
    public List<TbSysUser> findByConditions(LambdaQueryWrapper<TbSysUser> lambdaQueryWrapper) {
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public TbSysUser findOneByConditions(LambdaQueryWrapper<TbSysUser> lambdaQueryWrapper) {
        return this.baseMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public TbSysUser findById(Long id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public Page<TbSysUser> findPageByConditions(LambdaQueryWrapper<TbSysUser> lambdaQueryWrapper, int page, int size) {
        Page<TbSysUser> rowPage = new Page<>(page, size);
        return this.baseMapper.selectPage(rowPage, lambdaQueryWrapper);
    }

    @Override
    public Page<TbSysUser> findAll(int page, int size) {
        Page<TbSysUser> rowPage = new Page<>(page, size);
        return this.baseMapper.selectPage(rowPage, new LambdaQueryWrapper<>());
    }

    @Override
    public List<String> selectUserPostGroup(String userName) {
        return this.baseMapper.selectUserPostGroup(userName);
    }

    @Override
    public List<String> selectUserRoleGroup(String userName) {
        return this.baseMapper.selectUserRoleGroup(userName);
    }

    @Override
    public Page<TbSysUser> selectAllocatedList(SysUser sysUser, String delFlag, Integer page, Integer size) {
        Integer offset = null != page && page > 0 ? page * size : 0;
        List<TbSysUser> sysUserList = this.baseMapper.selectAllocatedList(sysUser, delFlag, offset, size);
        Long total = this.baseMapper.selectAllocatedListCount(sysUser, delFlag);
        Page<TbSysUser> tbSysUserPage = new Page<>();
        tbSysUserPage.setRecords(sysUserList);
        tbSysUserPage.setTotal(total);
        return tbSysUserPage;
    }

    @Override
    public List<TbSysUser> listWithDeptId(SysUser user) {
        return this.baseMapper.listWithDeptId(user);
    }

}
