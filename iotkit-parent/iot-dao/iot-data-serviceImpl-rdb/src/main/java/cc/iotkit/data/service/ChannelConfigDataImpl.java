package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.data.dao.ChannelConfigRepository;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.manager.IChannelConfigData;
import cc.iotkit.data.model.TbChannelConfig;
import cc.iotkit.model.notify.ChannelConfig;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * author: 石恒
 * date: 2023-05-11 17:43
 * description:
 **/
@Primary
@Service
public class ChannelConfigDataImpl implements IChannelConfigData, IJPACommData<ChannelConfig, Long> {

    @Resource
    private ChannelConfigRepository channelConfigRepository;

    @Override
    public JpaRepository getBaseRepository() {
        return channelConfigRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbChannelConfig.class;
    }

    @Override
    public Class getTClass() {
        return ChannelConfig.class;
    }

    @Override
    public Paging<ChannelConfig> findAll(PageRequest<ChannelConfig> pageRequest) {
        Page<TbChannelConfig> tbChannelConfigs = channelConfigRepository.findAll(Pageable.ofSize(pageRequest.getPageSize()).withPage(pageRequest.getPageNum() - 1));
        return new Paging<>(
                tbChannelConfigs.getTotalElements(),
                tbChannelConfigs.getContent()
        ).to(ChannelConfig.class);
    }
}
