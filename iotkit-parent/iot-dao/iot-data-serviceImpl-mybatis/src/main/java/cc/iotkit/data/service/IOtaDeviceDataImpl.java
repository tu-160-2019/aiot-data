package cc.iotkit.data.service;

import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.manager.IOtaDeviceData;
import cc.iotkit.data.model.TbOtaDevice;
import cc.iotkit.model.ota.OtaDevice;
import org.springframework.context.annotation.Primary;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: 石恒
 * @Date: 2023/5/25 23:41
 * @Description:
 */
@Primary
@Service
public class IOtaDeviceDataImpl implements IOtaDeviceData, IJPACommData<OtaDevice, Long> {
//public class IOtaDeviceDataImpl implements IOtaDeviceData, IJPACommData<OtaDevice, Long, TbOtaDevice> {

    @Resource
    private OtaDeviceService otaDeviceService;

    @Override
    public OtaDeviceService getBaseRepository() {
        return otaDeviceService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbOtaDevice.class;
    }

    @Override
    public Class getTClass() {
        return OtaDevice.class;
    }
}
