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

import cc.iotkit.data.manager.IDeviceInfoData;
import cc.iotkit.data.manager.IProductData;
import cc.iotkit.data.manager.ISpaceDeviceData;
import cc.iotkit.manager.model.vo.SpaceDeviceVo;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.product.Product;
import cc.iotkit.model.space.SpaceDevice;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SpaceDeviceService {
    @Autowired
    private ISpaceDeviceData spaceDeviceData;
    @Autowired
    @Qualifier("deviceInfoDataCache")
    private IDeviceInfoData deviceInfoData;
    @Autowired
    @Qualifier("productDataCache")
    private IProductData productData;
//TODO 没看到使用
//    public List<SpaceDeviceVo> getUserDevices(String uid, String spaceId) {
//        SpaceDevice device = new SpaceDevice();
//        device.setUid(uid);
//        List<SpaceDevice> spaceDevices;
//        if (StringUtils.isNotBlank(spaceId)) {
//            device.setSpaceId(spaceId);
//            spaceDevices = spaceDeviceData.findByUidAndSpaceIdOrderByAddAtDesc(uid, spaceId);
//        } else {
//            spaceDevices = spaceDeviceData.findBySpaceIdOrderByAddAtDesc(spaceId);
//        }
//
//        List<SpaceDeviceVo> spaceDeviceVos = new ArrayList<>();
//        spaceDevices.forEach(sd -> {
//            DeviceInfo deviceInfo = deviceInfoData.findByDeviceId(sd.getDeviceId());
//            Product product = productData.findByProductKey(deviceInfo.getProductKey());
//            spaceDeviceVos.add(SpaceDeviceVo.builder()
//                    .uid(sd.getUid())
//                    .deviceId(sd.getDeviceId())
//                    .name(sd.getName())
//                    .picUrl(product.getImg())
//                    .online(deviceInfo.getState().isOnline())
//                    .property(deviceInfo.getProperty())
//                    .productKey(deviceInfo.getProductKey())
//                    .build());
//        });
//        return spaceDeviceVos;
//    }
}
