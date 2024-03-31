package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.DeviceOtaInfoMapper;
import cc.iotkit.data.model.TbDeviceOtaInfo;
import cc.iotkit.data.service.DeviceOtaInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class DeviceOtaInfoServiceImpl extends ServiceImpl<DeviceOtaInfoMapper, TbDeviceOtaInfo> implements DeviceOtaInfoService {

}
