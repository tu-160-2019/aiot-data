package cc.iotkit.data.service;

import cc.iotkit.data.model.TbDeviceGroupMapping;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DeviceGroupMappingService extends IService<TbDeviceGroupMapping> {
    List<TbDeviceGroupMapping> findByConditions(LambdaQueryWrapper<TbDeviceGroupMapping> lambdaQueryWrapper);

    TbDeviceGroupMapping findOneByConditions(LambdaQueryWrapper<TbDeviceGroupMapping> lambdaQueryWrapper);
}
