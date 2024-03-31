package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.PluginInstanceMapper;
import cc.iotkit.data.model.TbPluginInstance;
import cc.iotkit.data.service.PluginInstanceService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class PluginInstanceServiceImpl extends ServiceImpl<PluginInstanceMapper, TbPluginInstance> implements PluginInstanceService {
}
