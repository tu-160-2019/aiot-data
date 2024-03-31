package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.SysMenuMapper;
import cc.iotkit.data.model.TbSysMenu;
import cc.iotkit.data.service.SysMenuService;
import cc.iotkit.model.system.SysMenu;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("DBSysMenuServiceImpl")
@Primary
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, TbSysMenu> implements SysMenuService {
    
    @Override
    public List<TbSysMenu> findByConditions(LambdaQueryWrapper<TbSysMenu> lambdaQueryWrapper) {
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public TbSysMenu findOneByConditions(LambdaQueryWrapper<TbSysMenu> lambdaQueryWrapper) {
        return this.baseMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public Page<TbSysMenu> findPageByConditions(LambdaQueryWrapper<TbSysMenu> lambdaQueryWrapper, int page, int size) {
        Page<TbSysMenu> rowPage = new Page<>(page, size);
        return this.baseMapper.selectPage(rowPage, lambdaQueryWrapper);
    }

    @Override
    public List<String> selectMenuPermsByRoleId(Long roleId) {
        return this.baseMapper.selectMenuPermsByRoleId(roleId);
    }

    @Override
    public List<TbSysMenu> selectMenuTreeByUserId(Long userId) {
        return this.baseMapper.selectMenuTreeByUserId(userId);
    }

    @Override
    public List<String> selectMenuPermsByUserId(Long userId) {
        return this.baseMapper.selectMenuPermsByUserId(userId);
    }

    @Override
    public List<TbSysMenu> selectMenuList(SysMenu menu, Long userId) {
        return this.baseMapper.selectMenuList(menu, userId);
    }

    @Override
    public List<Long> listParentIdByRoleId(Long roleId) {
        return this.baseMapper.listParentIdByRoleId(roleId);
    }

    @Override
    public List<Long> selectMenuListByRoleId(List<Long> roleIds, boolean menuCheckStrictly, Long parentRoleId) {
        return this.baseMapper.selectMenuListByRoleId(roleIds, menuCheckStrictly, parentRoleId);
    }
}
