package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.PluginInstanceRepository;
import cc.iotkit.data.manager.IPluginInstanceData;
import cc.iotkit.data.model.TbPluginInstance;
import cc.iotkit.model.plugin.PluginInstance;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * @author sjg
 */
@Primary
@Service
public class PluginInstanceDataImpl implements IPluginInstanceData, IJPACommData<PluginInstance, Long> {

    @Autowired
    private PluginInstanceRepository pluginInstanceRepository;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Override
    public JpaRepository getBaseRepository() {
        return pluginInstanceRepository;
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
        return MapstructUtils.convert(pluginInstanceRepository.findByMainIdAndPluginId(mainId, pluginId), PluginInstance.class);
    }
}
