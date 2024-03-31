package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.DeviceSubUserMapper;
import cc.iotkit.data.model.TbDeviceSubUser;
import cc.iotkit.data.service.DeviceSubUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class DeviceSubUserServiceImpl extends ServiceImpl<DeviceSubUserMapper, TbDeviceSubUser> implements DeviceSubUserService {
    @Override
    public List<TbDeviceSubUser> findByConditions(LambdaQueryWrapper<TbDeviceSubUser> lambdaQueryWrapper) {
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public TbDeviceSubUser findOneByConditions(LambdaQueryWrapper<TbDeviceSubUser> lambdaQueryWrapper) {
        return this.baseMapper.selectOne(lambdaQueryWrapper);
    }
}
