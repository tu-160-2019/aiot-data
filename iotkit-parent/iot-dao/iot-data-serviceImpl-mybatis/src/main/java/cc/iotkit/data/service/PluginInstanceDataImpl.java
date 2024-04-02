package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.manager.IPluginInstanceData;
import cc.iotkit.data.model.TbPluginInstance;
import cc.iotkit.model.plugin.PluginInstance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

@Primary
@Service
public class PluginInstanceDataImpl implements IPluginInstanceData, IJPACommData<PluginInstance, Long> {
//public class PluginInstanceDataImpl implements IPluginInstanceData, IJPACommData<PluginInstance, Long, TbPluginInstance> {

    @Autowired
    private PluginInstanceService pluginInstanceService;

    @Override
    public PluginInstanceService getBaseRepository() {
        return pluginInstanceService;
    }

    @Override
    public Class<TbPluginInstance> getJpaRepositoryClass() {
        return TbPluginInstance.class;
    }

    @Override
    public Class<PluginInstance> getTClass() {
        return PluginInstance.class;
    }

    @Override
    public PluginInstance findInstance(String mainId, String pluginId) {
        return MapstructUtils.convert(pluginInstanceService.getOne(new LambdaQueryWrapper<TbPluginInstance>()
                .eq(TbPluginInstance::getMainId, mainId).eq(TbPluginInstance::getPluginId, pluginId)), PluginInstance.class);

    }
}
