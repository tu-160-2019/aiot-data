package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.OtaDeviceMapper;
import cc.iotkit.data.model.TbOtaDevice;
import cc.iotkit.data.service.OtaDeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class OtaDeviceServiceImpl extends ServiceImpl<OtaDeviceMapper, TbOtaDevice> implements OtaDeviceService {
}
