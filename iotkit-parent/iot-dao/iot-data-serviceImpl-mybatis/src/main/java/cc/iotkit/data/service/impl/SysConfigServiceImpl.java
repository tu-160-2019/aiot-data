package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.SysConfigMapper;
import cc.iotkit.data.model.TbSysConfig;
import cc.iotkit.data.service.SysConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("DBSysConfigServiceImpl")
@Primary
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, TbSysConfig> implements SysConfigService {
    @Override
    public TbSysConfig findByConfigKey(String configKey) {
        return this.baseMapper.selectOne(new LambdaQueryWrapper<TbSysConfig>()
                .eq(TbSysConfig::getConfigKey, configKey));
    }

    @Override
    public Page<TbSysConfig> findAll(LambdaQueryWrapper<TbSysConfig> wrapper, int page, int size) {
        Page<TbSysConfig> rowPage = new Page<>(page, size);
        return this.baseMapper.selectPage(rowPage, wrapper);
    }

    @Override
    public List<TbSysConfig> findAllByCondition(LambdaQueryWrapper<TbSysConfig> wrapper) {
        return this.baseMapper.selectList(wrapper);
    }

    @Override
    public TbSysConfig findOneByCondition(LambdaQueryWrapper<TbSysConfig> wrapper) {
        return this.baseMapper.selectOne(wrapper);
    }
}
