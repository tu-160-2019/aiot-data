package cc.iotkit.data.service;

import cc.iotkit.data.model.TbChannelConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface ChannelConfigService extends IService<TbChannelConfig>  {
    Page<TbChannelConfig> findAll(LambdaQueryWrapper<TbChannelConfig> lambdaQueryWrapper, int page, int size);

}
