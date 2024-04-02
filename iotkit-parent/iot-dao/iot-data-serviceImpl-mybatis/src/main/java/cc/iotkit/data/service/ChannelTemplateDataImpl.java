package cc.iotkit.data.service;

import cc.iotkit.common.utils.MapstructUtils;

import cc.iotkit.data.mapper.IJPACommData;
import cc.iotkit.data.manager.IChannelTemplateData;
import cc.iotkit.data.model.TbChannelTemplate;
import cc.iotkit.model.notify.ChannelTemplate;
import org.springframework.context.annotation.Primary;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * author: 石恒
 * date: 2023-05-11 17:45
 * description:
 **/
@Primary
@Service
public class ChannelTemplateDataImpl implements IChannelTemplateData, IJPACommData<ChannelTemplate, Long> {
//public class ChannelTemplateDataImpl implements IChannelTemplateData, IJPACommData<ChannelTemplate, Long, TbChannelTemplate> {

    @Resource
    private ChannelTemplateService channelTemplateService;

    @Override
    public ChannelTemplateService getBaseRepository() {
        return channelTemplateService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbChannelTemplate.class;
    }

    @Override
    public Class getTClass() {
        return ChannelTemplate.class;
    }


    @Override
    public void batchSave(List<ChannelTemplate> data) {

    }

    @Override
    public long count() {
        return channelTemplateService.count();
    }

    @Override
    public List<ChannelTemplate> findAll() {
        return channelTemplateService.list().stream()
                .map(c -> MapstructUtils.convert(c, ChannelTemplate.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChannelTemplate> findAllByCondition(ChannelTemplate data) {
        return Collections.emptyList();
    }

    @Override
    public ChannelTemplate findOneByCondition(ChannelTemplate data) {
        return null;
    }


}
