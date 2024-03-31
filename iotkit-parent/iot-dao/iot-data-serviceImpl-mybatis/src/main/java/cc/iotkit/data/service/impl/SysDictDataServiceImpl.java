package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.SysDictDataMapper;
import cc.iotkit.data.model.TbSysDictData;
import cc.iotkit.data.service.SysDictDataService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("DBSysDictDataServiceImpl")
@Primary
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, TbSysDictData> implements SysDictDataService {
    @Override
    public List<TbSysDictData> findByCondition(LambdaQueryWrapper<TbSysDictData> wrapper) {
        return this.baseMapper.selectList(wrapper);
    }

    @Override
    public Page<TbSysDictData> findPageByCondition(LambdaQueryWrapper<TbSysDictData> wrapper, int page, int size) {
        Page<TbSysDictData> rowPage = new Page<>(page, size);
        return this.baseMapper.selectPage(rowPage, wrapper);
    }
}
