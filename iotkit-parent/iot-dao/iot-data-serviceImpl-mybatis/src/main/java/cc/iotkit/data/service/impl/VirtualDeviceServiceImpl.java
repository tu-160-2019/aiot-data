package cc.iotkit.data.service.impl;

import cc.iotkit.data.mapper.VirtualDeviceMapper;
import cc.iotkit.data.model.TbVirtualDevice;
import cc.iotkit.data.service.VirtualDeviceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class VirtualDeviceServiceImpl extends ServiceImpl<VirtualDeviceMapper, TbVirtualDevice> implements VirtualDeviceService {

    @Override
    public List<TbVirtualDevice> findByTriggerAndState(String trigger, String state) {
        return this.baseMapper.selectList(new LambdaQueryWrapper<TbVirtualDevice>()
                .eq(TbVirtualDevice::getTrigger, trigger)
                .eq(TbVirtualDevice::getState, state));
    }

    @Override
    public Page<TbVirtualDevice> findByUid(String uid, int size, int page) {
        Page<TbVirtualDevice> rowPage = new Page<>(page, size);
        return this.baseMapper.selectPage(rowPage,
                new LambdaQueryWrapper<TbVirtualDevice>().eq(TbVirtualDevice::getUid, uid));

    }
}
