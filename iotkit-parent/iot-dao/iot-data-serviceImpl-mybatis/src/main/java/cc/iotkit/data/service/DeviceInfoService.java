package cc.iotkit.data.service;

import cc.iotkit.data.model.TbDeviceInfo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DeviceInfoService extends IService<TbDeviceInfo> {
    List<TbDeviceInfo> findByConditions(LambdaQueryWrapper<TbDeviceInfo> lambdaQueryWrapper);

    TbDeviceInfo findOneByConditions(LambdaQueryWrapper<TbDeviceInfo> lambdaQueryWrapper);

    List<TbDeviceInfo> findByProductNodeType(String uid, Integer nodeType);
}
