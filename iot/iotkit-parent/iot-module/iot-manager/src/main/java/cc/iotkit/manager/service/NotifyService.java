package cc.iotkit.manager.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.manager.IChannelConfigData;
import cc.iotkit.data.manager.IChannelData;
import cc.iotkit.data.manager.IChannelTemplateData;
import cc.iotkit.data.manager.INotifyMessageData;
import cc.iotkit.manager.dto.bo.channel.ChannelConfigBo;
import cc.iotkit.manager.dto.bo.channel.ChannelTemplateBo;
import cc.iotkit.manager.dto.vo.channel.ChannelConfigVo;
import cc.iotkit.manager.dto.vo.channel.ChannelTemplateVo;
import cc.iotkit.model.notify.Channel;
import cc.iotkit.model.notify.ChannelConfig;
import cc.iotkit.model.notify.ChannelTemplate;
import cc.iotkit.model.notify.NotifyMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * author: 石恒
 * date: 2023-05-11 15:21
 * description:
 **/
@Slf4j
@Service
public class NotifyService {

    @Resource
    private IChannelData iChannelData;

    @Resource
    private IChannelConfigData iChannelConfigData;

    @Resource
    private IChannelTemplateData iChannelTemplateData;

    @Resource
    private INotifyMessageData iNotifyMessageData;

    public List<Channel> getChannelList() {
        return iChannelData.findAll();
    }

    public Paging<ChannelConfigVo> getChannelConfigList(PageRequest<ChannelConfigBo> request) {
        return iChannelConfigData.findAll(request.to(ChannelConfig.class)).to(ChannelConfigVo.class);
    }

    public List<ChannelConfigVo> getChannelConfigAll() {
        return MapstructUtils.convert(iChannelConfigData.findAll(),ChannelConfigVo.class);
    }

    public ChannelConfig addChannelConfig(ChannelConfig channelConfig) {
        return iChannelConfigData.save(channelConfig);
    }

    public ChannelConfig getChannelConfigById(Long id) {
        return iChannelConfigData.findById(id);
    }

    public ChannelConfig updateChannelConfigById(ChannelConfig channelConfig) {
        return iChannelConfigData.save(channelConfig);
    }

    public Boolean delChannelConfigById(Long id) {
        iChannelConfigData.deleteById(id);
        return Boolean.TRUE;
    }

    public  Paging<ChannelTemplateVo> getChannelTemplateList(PageRequest<ChannelTemplateBo> request) {
        return iChannelTemplateData.findAll(request.to(ChannelTemplate.class)).to(ChannelTemplateVo.class);
    }

    public ChannelTemplate addChannelTemplate(ChannelTemplateBo channelTemplate) {
        return iChannelTemplateData.save(channelTemplate.to(ChannelTemplate.class));
    }

    public ChannelTemplate getChannelTemplateById(Long id) {
        return iChannelTemplateData.findById(id);
    }

    public ChannelTemplate updateChannelTemplateById(ChannelTemplate channelTemplate) {
        return iChannelTemplateData.save(channelTemplate);
    }

    public Boolean delChannelTemplateById(Long id) {
        iChannelTemplateData.deleteById(id);
        return Boolean.TRUE;
    }

    public Paging<NotifyMessage> getNotifyMessageList(PageRequest<NotifyMessage> request) {
        return iNotifyMessageData.findAll(request);
    }
}
