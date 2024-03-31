package cc.iotkit.data.service;

import cc.iotkit.data.mapper.IJPACommData;
import cc.iotkit.data.manager.IDeviceOtaDetailData;
import cc.iotkit.data.model.TbDeviceOtaDetail;
import cc.iotkit.model.ota.DeviceOtaDetail;
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
public class DeviceOtaDetailDataImpl implements IDeviceOtaDetailData, IJPACommData<DeviceOtaDetail, Long, TbDeviceOtaDetail> {

    private final DeviceOtaDetailService deviceOtaDetailService;

    @Override
    public DeviceOtaDetailService getBaseRepository() {
        return deviceOtaDetailService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbDeviceOtaDetail.class;
    }

    @Override
    public Class getTClass() {
        return DeviceOtaDetail.class;
    }
}
