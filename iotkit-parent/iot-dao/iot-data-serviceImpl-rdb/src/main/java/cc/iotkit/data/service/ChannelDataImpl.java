package cc.iotkit.data.service;

import cc.iotkit.data.dao.ChannelRepository;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.manager.IChannelData;
import cc.iotkit.data.model.TbChannel;
import cc.iotkit.model.notify.Channel;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * author: 石恒
 * date: 2023-05-11 17:44
 * description:
 **/
@Primary
@Service
public class ChannelDataImpl implements IChannelData, IJPACommData<Channel, Long> {

    @Resource
    private ChannelRepository channelRepository;

    @Override
    public JpaRepository getBaseRepository() {
        return channelRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbChannel.class;
    }

    @Override
    public Class getTClass() {
        return Channel.class;
    }



}
