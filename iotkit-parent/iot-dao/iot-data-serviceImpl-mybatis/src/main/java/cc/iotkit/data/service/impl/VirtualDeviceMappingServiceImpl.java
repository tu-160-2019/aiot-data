package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.VirtualDeviceMappingMapper;
import cc.iotkit.data.model.TbVirtualDeviceMapping;
import cc.iotkit.data.service.VirtualDeviceMappingService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Primary
public class VirtualDeviceMappingServiceImpl extends ServiceImpl<VirtualDeviceMappingMapper, TbVirtualDeviceMapping> implements VirtualDeviceMappingService {
    @Override
    @Transactional
    public void deleteByVirtualId(String virtualId) {
        this.baseMapper.delete(new LambdaQueryWrapper<TbVirtualDeviceMapping>()
                .eq(TbVirtualDeviceMapping::getVirtualId, virtualId));
    }

    @Override
    public List<TbVirtualDeviceMapping> listByVirtualId(String virtualId) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<TbVirtualDeviceMapping>()
                .eq(TbVirtualDeviceMapping::getVirtualId, virtualId));
    }
}
