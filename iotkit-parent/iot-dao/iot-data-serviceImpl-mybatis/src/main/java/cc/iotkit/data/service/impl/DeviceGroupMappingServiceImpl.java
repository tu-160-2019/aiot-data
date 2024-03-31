package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.DeviceGroupMappingMapper;
import cc.iotkit.data.model.TbDeviceGroupMapping;
import cc.iotkit.data.service.DeviceGroupMappingService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class DeviceGroupMappingServiceImpl extends ServiceImpl<DeviceGroupMappingMapper, TbDeviceGroupMapping> implements DeviceGroupMappingService {
    @Override
    public List<TbDeviceGroupMapping> findByConditions(LambdaQueryWrapper<TbDeviceGroupMapping> lambdaQueryWrapper) {
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public TbDeviceGroupMapping findOneByConditions(LambdaQueryWrapper<TbDeviceGroupMapping> lambdaQueryWrapper) {
        return this.baseMapper.selectOne(lambdaQueryWrapper);
    }
}
