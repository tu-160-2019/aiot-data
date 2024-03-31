package cc.iotkit.data.service;

import cc.iotkit.data.model.TbDeviceConfig;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DeviceConfigService extends IService<TbDeviceConfig> {

    TbDeviceConfig findByProductKeyAndDeviceName(String productKey, String deviceName);

    TbDeviceConfig findByDeviceId(String deviceId);
}
