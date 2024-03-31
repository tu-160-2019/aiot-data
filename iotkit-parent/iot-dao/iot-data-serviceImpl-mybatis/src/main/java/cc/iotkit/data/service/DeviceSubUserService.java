package cc.iotkit.data.service;

import cc.iotkit.data.model.TbDeviceSubUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DeviceSubUserService extends IService<TbDeviceSubUser> {
    List<TbDeviceSubUser> findByConditions(LambdaQueryWrapper<TbDeviceSubUser> lambdaQueryWrapper);

    TbDeviceSubUser findOneByConditions(LambdaQueryWrapper<TbDeviceSubUser> lambdaQueryWrapper);
}
