package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.DeviceConfigMapper;
import cc.iotkit.data.model.TbDeviceConfig;
import cc.iotkit.data.service.DeviceConfigService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class DeviceConfigServiceImpl extends ServiceImpl<DeviceConfigMapper, TbDeviceConfig> implements DeviceConfigService {

    @Override
    public TbDeviceConfig findByProductKeyAndDeviceName(String productKey, String deviceName) {
        return this.baseMapper.selectOne(new LambdaQueryWrapper<TbDeviceConfig>()
                .eq(TbDeviceConfig::getProductKey, productKey)
                .eq(TbDeviceConfig::getDeviceName, deviceName));
    }

    @Override
    public TbDeviceConfig findByDeviceId(String deviceId) {
        return this.baseMapper.selectOne(new LambdaQueryWrapper<TbDeviceConfig>().eq(TbDeviceConfig::getDeviceId, deviceId));
    }
}
