package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.SysDictTypeMapper;
import cc.iotkit.data.model.TbSysDictType;
import cc.iotkit.data.service.SysDictTypeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("DBSysDictTypeServiceImpl")
@Primary
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, TbSysDictType> implements SysDictTypeService {
    @Override
    public List<TbSysDictType> findByConditions(LambdaQueryWrapper<TbSysDictType> lambdaQueryWrapper) {
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public TbSysDictType findOneByConditions(LambdaQueryWrapper<TbSysDictType> lambdaQueryWrapper) {
        return this.baseMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public Page<TbSysDictType> findPageByConditions(LambdaQueryWrapper<TbSysDictType> lambdaQueryWrapper, int page, int size) {
        Page<TbSysDictType> rowPage = new Page<>(page, size);
        return this.baseMapper.selectPage(rowPage, lambdaQueryWrapper);
    }
}
