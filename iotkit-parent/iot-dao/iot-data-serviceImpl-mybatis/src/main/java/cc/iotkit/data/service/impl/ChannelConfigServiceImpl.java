package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.ChannelConfigMapper;
import cc.iotkit.data.model.TbChannelConfig;
import cc.iotkit.data.service.ChannelConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class ChannelConfigServiceImpl extends ServiceImpl<ChannelConfigMapper, TbChannelConfig> implements ChannelConfigService {

    public Page<TbChannelConfig> findAll(LambdaQueryWrapper<TbChannelConfig> lambdaQueryWrapper, int page, int size) {
        Page<TbChannelConfig> rowPage = new Page<>(page, size);
        return this.baseMapper.selectPage(rowPage, lambdaQueryWrapper);
    }

}
