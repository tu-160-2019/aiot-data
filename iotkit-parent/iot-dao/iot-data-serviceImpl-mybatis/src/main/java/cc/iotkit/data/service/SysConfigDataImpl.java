package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.model.TbSysConfig;
import cc.iotkit.data.system.ISysConfigData;
import cc.iotkit.model.system.SysConfig;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.IteratorUtils;
import org.springframework.context.annotation.Primary;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Primary
@Service
@RequiredArgsConstructor
public class SysConfigDataImpl implements ISysConfigData, IJPACommData<SysConfig, Long, TbSysConfig> {

    @Qualifier("DBSysConfigServiceImpl")
    private final SysConfigService baseService;


    @Override
    public SysConfigService getBaseRepository() {
        return baseService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSysConfig.class;
    }

    @Override
    public Class getTClass() {
        return SysConfig.class;
    }

    @Override
    public SysConfig findById(Long id) {
        TbSysConfig tbSysConfig = baseService.getById(id);
        return MapstructUtils.convert(tbSysConfig, SysConfig.class);
    }

    @Override
    public SysConfig save(SysConfig data) {
        baseService.save(MapstructUtils.convert(data, TbSysConfig.class));
        return data;
    }

    @Override
    public List<SysConfig> findByIds(Collection<Long> id) {
        Iterable<TbSysConfig> allById = baseService.listByIds(id);
        Iterator<TbSysConfig> iterator = allById.iterator();
        return MapstructUtils.convert(IteratorUtils.toList(iterator), SysConfig.class);
    }



    @Override
    public Paging<SysConfig> findAll(PageRequest<SysConfig> pageRequest) {
        SysConfig query = pageRequest.getData();

        LambdaQueryWrapper<TbSysConfig> wrapper = new LambdaQueryWrapper<TbSysConfig>()
                .eq(StringUtils.isNotBlank(query.getConfigName()), TbSysConfig::getConfigName, query.getConfigName())
                .eq(StringUtils.isNotBlank(query.getConfigType()), TbSysConfig::getConfigType, query.getConfigType())
                .eq(StringUtils.isNotBlank(query.getConfigKey()), TbSysConfig::getConfigKey, query.getConfigKey());

        Page<TbSysConfig> all = baseService.findAll(wrapper, pageRequest.getPageNum(), pageRequest.getPageSize());
        return new Paging<>(all.getTotal(),
                MapstructUtils.convert(all.getRecords(), SysConfig.class));

    }


    @Override
    public List<SysConfig> findAllByCondition(SysConfig data) {
        List<TbSysConfig> tbSysConfigList = baseService.findAllByCondition(new LambdaQueryWrapper<TbSysConfig>()
                .eq(StringUtils.isNotBlank(data.getConfigKey()), TbSysConfig::getConfigKey, data.getConfigKey()));
        return MapstructUtils.convert(tbSysConfigList, SysConfig.class);
    }

    @Override
    public SysConfig findOneByCondition(SysConfig data) {
        TbSysConfig tbSysConfig = baseService.findOneByCondition(new LambdaQueryWrapper<TbSysConfig>()
                .eq(StringUtils.isNotBlank(data.getConfigKey()), TbSysConfig::getConfigKey, data.getConfigKey()));
        return MapstructUtils.convert(tbSysConfig, SysConfig.class);
    }

    @Override
    public SysConfig findByConfigKey(String configKey) {
        TbSysConfig tbSysConfig = baseService.findByConfigKey(configKey);
        return MapstructUtils.convert(tbSysConfig, SysConfig.class);
    }

    @Override
    public Paging<SysConfig> findAllByConditions(PageRequest<SysConfig> pageRequest) {
        LambdaQueryWrapper<TbSysConfig> wrapper = new LambdaQueryWrapper<TbSysConfig>()
                .eq(StringUtils.isNotBlank(pageRequest.getData().getConfigKey()), TbSysConfig::getConfigKey, pageRequest.getData().getConfigKey())
                .eq(StringUtils.isNotBlank(pageRequest.getData().getConfigName()), TbSysConfig::getConfigName, pageRequest.getData().getConfigName())
                .eq(StringUtils.isNotBlank(pageRequest.getData().getConfigType()), TbSysConfig::getConfigType, pageRequest.getData().getConfigType());

        Page<TbSysConfig> all = baseService.findAll(wrapper, pageRequest.getPageNum(), pageRequest.getPageSize());

        return new Paging<>(all.getTotal(),
                MapstructUtils.convert(all.getRecords(), SysConfig.class));

    }

}
