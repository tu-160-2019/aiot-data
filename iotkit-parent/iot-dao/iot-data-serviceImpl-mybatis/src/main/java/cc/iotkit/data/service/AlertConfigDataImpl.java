package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.IJPACommData;
import cc.iotkit.data.manager.IAlertConfigData;
import cc.iotkit.data.model.TbAlertConfig;
import cc.iotkit.model.alert.AlertConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Primary
@Service
public class AlertConfigDataImpl implements IAlertConfigData, IJPACommData<AlertConfig, Long, TbAlertConfig> {

    @Autowired
    private AlertConfigService alertConfigService;


    @Override
    public AlertConfigService getBaseRepository() {
        return alertConfigService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbAlertConfig.class;
    }

    @Override
    public Class getTClass() {
        return AlertConfig.class;
    }


    @Override
    public Paging<AlertConfig> selectAlertConfigPage(PageRequest<AlertConfig> request) {
//        Page<TbAlertConfig> alertConfigPage = alertConfigRepository.findAll(Pageable.ofSize(request.getPageSize()).withPage(request.getPageNum() - 1));
//        return new Paging<>(alertConfigPage.getTotalElements(), MapstructUtils.convert(alertConfigPage.getContent(), AlertConfig.class));
        return null;
    }
}
