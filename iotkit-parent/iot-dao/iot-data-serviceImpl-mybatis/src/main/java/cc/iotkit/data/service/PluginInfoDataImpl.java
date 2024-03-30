package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.PluginInfoRepository;
import cc.iotkit.data.manager.IPluginInfoData;
import cc.iotkit.data.model.TbPluginInfo;
import cc.iotkit.data.util.PageBuilder;
import cc.iotkit.data.util.PredicateBuilder;
import cc.iotkit.model.plugin.PluginInfo;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import static cc.iotkit.data.model.QTbPluginInfo.tbPluginInfo;


/**
 * @author sjg
 */
@Primary
@Service
public class PluginInfoDataImpl implements IPluginInfoData, IJPACommData<PluginInfo, Long> {

    @Autowired
    private PluginInfoRepository pluginInfoRepository;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Override
    public JpaRepository getBaseRepository() {
        return pluginInfoRepository;
    }

    @Override
    public Class<TbPluginInfo> getJpaRepositoryClass() {
        return TbPluginInfo.class;
    }

    @Override
    public Class<PluginInfo> getTClass() {
        return PluginInfo.class;
    }

    @Override
    public PluginInfo findByPluginId(String pluginId) {
        return MapstructUtils.convert(pluginInfoRepository.findByPluginId(pluginId), PluginInfo.class);
    }

    @Override
    public Paging<PluginInfo> findAll(PageRequest<PluginInfo> pageRequest) {
        return PageBuilder.toPaging(pluginInfoRepository.findAll(
                buildQueryCondition(pageRequest.getData()),
                PageBuilder.toPageable(pageRequest)
        )).to(PluginInfo.class);
    }

    private Predicate buildQueryCondition(PluginInfo data) {
        return PredicateBuilder.instance()
                .and(StringUtils.isNotBlank(data.getType()), () -> tbPluginInfo.type.eq(data.getType()))
                .and(data.getState() != null, () -> tbPluginInfo.state.eq(data.getState()))
                .build();
    }
}
