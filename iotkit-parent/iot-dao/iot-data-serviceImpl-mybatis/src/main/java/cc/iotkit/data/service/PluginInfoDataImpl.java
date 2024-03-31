package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.manager.IPluginInfoData;
import cc.iotkit.data.model.TbPluginInfo;
import cc.iotkit.model.plugin.PluginInfo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

import org.springframework.stereotype.Service;



/**
 * @author sjg
 */
@Primary
@Service
public class PluginInfoDataImpl implements IPluginInfoData, IJPACommData<PluginInfo, Long, TbPluginInfo> {

    @Autowired
    private PluginInfoService pluginInfoService;

    @Override
    public PluginInfoService getBaseRepository() {
        return pluginInfoService;
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
        return MapstructUtils.convert(pluginInfoService.getOne(new LambdaQueryWrapper<TbPluginInfo>().eq(TbPluginInfo::getPluginId, pluginId)), PluginInfo.class);
    }

    @Override
    public Paging<PluginInfo> findAll(PageRequest<PluginInfo> pageRequest) {
        LambdaQueryWrapper<TbPluginInfo> wrapper = new LambdaQueryWrapper<TbPluginInfo>();
        wrapper.eq(TbPluginInfo::getType, pageRequest.getData().getType());
        wrapper.eq(TbPluginInfo::getState, pageRequest.getData().getState());

        Page<TbPluginInfo> rowPage = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        Page<TbPluginInfo> page = pluginInfoService.page(rowPage, wrapper);

        return new Paging<>(page.getTotal(), MapstructUtils.convert(page.getRecords(), PluginInfo.class));
    }

}
