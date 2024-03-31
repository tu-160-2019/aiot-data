package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.DeviceOtaDetailMapper;
import cc.iotkit.data.model.TbDeviceOtaDetail;
import cc.iotkit.data.service.DeviceOtaDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class DeviceOtaDetailServiceImpl extends ServiceImpl<DeviceOtaDetailMapper, TbDeviceOtaDetail> implements DeviceOtaDetailService {
}
