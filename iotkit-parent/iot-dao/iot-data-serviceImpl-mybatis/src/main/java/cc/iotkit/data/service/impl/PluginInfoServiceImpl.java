package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.PluginInfoMapper;
import cc.iotkit.data.model.TbPluginInfo;
import cc.iotkit.data.service.PluginInfoService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

@Service
public class PluginInfoServiceImpl extends ServiceImpl<PluginInfoMapper, TbPluginInfo> implements PluginInfoService {
}
