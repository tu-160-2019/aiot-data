/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.manager.controller;

import cc.iotkit.common.satoken.utils.AuthUtil;
import cc.iotkit.data.manager.ICategoryData;
import cc.iotkit.data.manager.IDeviceInfoData;
import cc.iotkit.data.manager.IProductData;
import cc.iotkit.manager.model.stats.MainStats;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.temporal.IThingModelMessageData;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"状态"})
@Slf4j
@RestController
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private ICategoryData ICategoryData;
    @Autowired
    @Qualifier("productDataCache")
    private IProductData productData;
    @Autowired
    @Qualifier("deviceInfoDataCache")
    private IDeviceInfoData deviceInfoData;
    @Autowired
    private IThingModelMessageData thingModelMessageData;

    @PostMapping("/main")
    public MainStats getMainStats() {
        MainStats mainStats = new MainStats();
        String uid = AuthUtil.getUserId();

        long now = System.currentTimeMillis();
        if (AuthUtil.isAdmin()) {
            mainStats.setCategoryTotal(ICategoryData.count());
            mainStats.setProductTotal(productData.count());
            mainStats.setDeviceTotal(deviceInfoData.count());

            DeviceInfo condition = new DeviceInfo();
            DeviceInfo.State state = new DeviceInfo.State();
            state.setOnline(true);
            condition.setState(state);
            mainStats.setOnlineTotal(deviceInfoData.findAllByCondition(condition).size());

            state.setOnline(false);
            condition.setState(state);
            mainStats.setOfflineTotal(deviceInfoData.findAllByCondition(condition).size());

            // 待激活设备
            mainStats.setNeverOnlineTotal(deviceInfoData.findNeverUsedDevices().size());

            mainStats.setReportTotal(thingModelMessageData.count());
            //上报数据统计
            mainStats.setReportDataStats(thingModelMessageData.getDeviceMessageStatsWithUid(null, now - 48 * 3600 * 1000, now));
            //产品数量统计
            mainStats.setDeviceStatsOfCategory(deviceInfoData.getDeviceStatsByCategory(""));
        } else {
            mainStats.setCategoryTotal(ICategoryData.count());
//            mainStats.setProductTotal(productData.countByUid(uid));
            mainStats.setDeviceTotal(deviceInfoData.countByUid(uid));
//            mainStats.setReportTotal(deviceReportRepository.countByUid(uid));
            //上报数据统计
            mainStats.setReportDataStats(thingModelMessageData.getDeviceMessageStatsWithUid(uid, now - 48 * 3600 * 1000, now));
            //产品数量统计
            mainStats.setDeviceStatsOfCategory(deviceInfoData.getDeviceStatsByCategory(uid));
        }

        return mainStats;
    }

}
