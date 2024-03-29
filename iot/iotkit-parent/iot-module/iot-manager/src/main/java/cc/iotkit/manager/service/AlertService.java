/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.manager.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.api.Request;
import cc.iotkit.data.manager.IAlertConfigData;
import cc.iotkit.data.manager.IAlertRecordData;
import cc.iotkit.model.alert.AlertConfig;
import cc.iotkit.model.alert.AlertRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AlertService {

    @Autowired
    private IAlertConfigData alertConfigData;
    @Autowired
    private IAlertRecordData alertRecordData;

    public AlertConfig createAlertConfig(AlertConfig alertConfig) {
        return alertConfigData.save(alertConfig);
    }

    public AlertConfig updateAlertConfig(AlertConfig alertConfig) {
        return alertConfigData.save(alertConfig);
    }

    public void deleteAlertConfigById(Long id) {
        alertConfigData.deleteById(id);
    }

    public Paging<AlertConfig> selectAlertConfigPage(PageRequest<AlertConfig> request) {
        return alertConfigData.selectAlertConfigPage(request);
    }

    public Paging<AlertRecord> selectAlertRecordPage(PageRequest<AlertRecord> request) {
        return alertRecordData.selectAlertConfigPage(request);
    }

    public void addAlert(AlertConfig config, String content) {
        alertRecordData.save(AlertRecord.builder()
                .level(config.getLevel())
                .name(config.getName())
                .readFlg(false)
                .alertTime(System.currentTimeMillis())
                .details(content)
                .build());
    }
}
