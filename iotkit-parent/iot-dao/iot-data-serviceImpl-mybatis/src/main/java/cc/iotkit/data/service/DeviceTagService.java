package cc.iotkit.data.service;

import cc.iotkit.data.model.TbDeviceTag;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DeviceTagService extends IService<TbDeviceTag> {

    List<TbDeviceTag> findByConditions(LambdaQueryWrapper<TbDeviceTag> lambdaQueryWrapper);

    TbDeviceTag findOneByConditions(LambdaQueryWrapper<TbDeviceTag> lambdaQueryWrapper);
}
