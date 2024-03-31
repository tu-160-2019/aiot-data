package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.DeviceTagMapper;
import cc.iotkit.data.model.TbDeviceTag;
import cc.iotkit.data.service.DeviceTagService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class DeviceTagServiceImpl extends ServiceImpl<DeviceTagMapper, TbDeviceTag> implements DeviceTagService {
    @Override
    public List<TbDeviceTag> findByConditions(LambdaQueryWrapper<TbDeviceTag> lambdaQueryWrapper) {
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public TbDeviceTag findOneByConditions(LambdaQueryWrapper<TbDeviceTag> lambdaQueryWrapper) {
        return this.baseMapper.selectOne(lambdaQueryWrapper);
    }
}
