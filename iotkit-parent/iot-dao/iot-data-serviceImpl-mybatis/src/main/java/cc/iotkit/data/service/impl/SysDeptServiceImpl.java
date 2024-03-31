package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.SysDeptMapper;
import cc.iotkit.data.model.TbSysDept;
import cc.iotkit.data.service.SysDeptService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("DBSysDeptServiceImpl")
@Primary
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, TbSysDept> implements SysDeptService {

    @Override
    public List<TbSysDept> findByConditions(LambdaQueryWrapper<TbSysDept> lambdaQueryWrapper) {
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public TbSysDept findOneByConditions(LambdaQueryWrapper<TbSysDept> lambdaQueryWrapper) {
        return this.baseMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public Page<TbSysDept> findPageByConditions(LambdaQueryWrapper<TbSysDept> lambdaQueryWrapper, int page, int size) {
        Page<TbSysDept> rowPage = new Page<>(page, size);
        return this.baseMapper.selectPage(rowPage, lambdaQueryWrapper);
    }

    @Override
    public long countByParentId(Long parentId) {
        return this.baseMapper.selectCount(new LambdaQueryWrapper<TbSysDept>().eq(TbSysDept::getParentId, parentId));
    }

    @Override
    public List<TbSysDept> findByRoleId(Long roleId) {
        return this.baseMapper.findByRoleId(roleId);
    }
}
