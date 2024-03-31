package cc.iotkit.data.service.impl;

import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.DeviceGroupMapper;
import cc.iotkit.data.model.TbDeviceGroup;
import cc.iotkit.data.service.DeviceGroupService;
import cc.iotkit.model.device.DeviceGroup;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class DeviceGroupServiceImpl extends ServiceImpl<DeviceGroupMapper, TbDeviceGroup> implements DeviceGroupService {
    @Override
    public Paging<DeviceGroup> findByNameLike(String name, int page, int size) {
        Page<TbDeviceGroup> rowPage = new Page<>(page, size);
        Page<TbDeviceGroup> deviceGroupPage = this.baseMapper.selectPage(rowPage, new LambdaQueryWrapper<TbDeviceGroup>().like(TbDeviceGroup::getName, name));

        Paging<DeviceGroup> paging = new Paging<>();
        paging.setRows(MapstructUtils.convert(deviceGroupPage.getRecords(), DeviceGroup.class));
        paging.setTotal(rowPage.getTotal());
        return paging;
    }
}
