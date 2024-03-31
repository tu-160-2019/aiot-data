package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.DeviceInfoMapper;
import cc.iotkit.data.model.TbDeviceInfo;
import cc.iotkit.data.service.DeviceInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class DeviceInfoServiceImpl extends ServiceImpl<DeviceInfoMapper, TbDeviceInfo> implements DeviceInfoService {
    @Override
    public List<TbDeviceInfo> findByConditions(LambdaQueryWrapper<TbDeviceInfo> lambdaQueryWrapper) {
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    @Override
    public TbDeviceInfo findOneByConditions(LambdaQueryWrapper<TbDeviceInfo> lambdaQueryWrapper) {
        return this.baseMapper.selectOne(lambdaQueryWrapper);
    }

    @Override
    public List<TbDeviceInfo> findByProductNodeType(String uid, Integer nodeType) {
        return baseMapper.findByProductNodeType(uid, nodeType);
    }
}
