package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.IJPACommData;
import cc.iotkit.data.manager.IChannelConfigData;
import cc.iotkit.data.model.TbChannelConfig;
import cc.iotkit.model.notify.ChannelConfig;
import org.springframework.context.annotation.Primary;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import javax.annotation.Resource;

/**
 * author: 石恒
 * date: 2023-05-11 17:43
 * description:
 **/
@Primary
@Service
public class ChannelConfigDataImpl implements IChannelConfigData, IJPACommData<ChannelConfig, Long, TbChannelConfig> {

    @Resource
    private ChannelConfigService channelConfigService;

    @Override
    public ChannelConfigService getBaseRepository() {
        return channelConfigService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbChannelConfig.class;
    }

    @Override
    public Class getTClass() {
        return ChannelConfig.class;
    }

    @Override
    public Paging<ChannelConfig> findAll(PageRequest<ChannelConfig> pageRequest) {

        TbChannelConfig tbChannelConfig = MapstructUtils.convert(pageRequest.getData(), TbChannelConfig.class);

        Page<TbChannelConfig> paged = channelConfigService.findAll(new LambdaQueryWrapper<>(tbChannelConfig), pageRequest.getPageNum(), pageRequest.getPageSize());

        return new Paging<>(paged.getTotal(), MapstructUtils.convert(paged.getRecords(), ChannelConfig.class));

    }
}
