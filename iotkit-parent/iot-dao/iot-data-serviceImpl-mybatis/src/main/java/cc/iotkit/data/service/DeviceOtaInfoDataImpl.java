package cc.iotkit.data.service;

import cc.iotkit.data.mapper.IJPACommData;
import cc.iotkit.data.manager.IDeviceOtaInfoData;
import cc.iotkit.data.model.TbDeviceOtaInfo;
import cc.iotkit.model.ota.DeviceOtaInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * @Author: 石恒
 * @Date: 2023/6/15 22:19
 * @Description:
 */
@Primary
@Service
@RequiredArgsConstructor
public class DeviceOtaInfoDataImpl implements IDeviceOtaInfoData, IJPACommData<DeviceOtaInfo, Long> {
//public class DeviceOtaInfoDataImpl implements IDeviceOtaInfoData, IJPACommData<DeviceOtaInfo, Long, TbDeviceOtaInfo> {

    private final DeviceOtaInfoService deviceOtaInfoService;

    @Override
    public DeviceOtaInfoService getBaseRepository() {
        return deviceOtaInfoService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbDeviceOtaInfo.class;
    }

    @Override
    public Class getTClass() {
        return DeviceOtaInfo.class;
    }
}
