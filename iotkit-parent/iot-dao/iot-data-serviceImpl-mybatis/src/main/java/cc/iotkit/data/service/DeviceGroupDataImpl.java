package cc.iotkit.data.service;

import cc.iotkit.common.api.Paging;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.mapper.IJPACommData;
import cc.iotkit.data.manager.IDeviceGroupData;
import cc.iotkit.data.model.TbDeviceGroup;
import cc.iotkit.model.device.DeviceGroup;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Primary
@Service
public class DeviceGroupDataImpl implements IDeviceGroupData, IJPACommData<DeviceGroup, String> {
//public class DeviceGroupDataImpl implements IDeviceGroupData, IJPACommData<DeviceGroup, String, TbDeviceGroup> {

    @Autowired
    private DeviceGroupService deviceGroupService;

    @Override
    public DeviceGroupService getBaseRepository() {
        return deviceGroupService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbDeviceGroup.class;
    }

    @Override
    public Class getTClass() {
        return DeviceGroup.class;
    }

    @Override
    public Paging<DeviceGroup> findByNameLike(String name, int page, int size) {
        return deviceGroupService.findByNameLike(name, page, size);
    }


    @Override
    public DeviceGroup findById(String s) {
        return MapstructUtils.convert(deviceGroupService.getById(s), DeviceGroup.class);
    }

    @Override
    public DeviceGroup save(DeviceGroup data) {
        if (StringUtils.isBlank(data.getId())) {
            data.setId(UUID.randomUUID().toString());
        }
        deviceGroupService.save(MapstructUtils.convert(data, TbDeviceGroup.class));
        return data;
    }


    @Override
    public List<DeviceGroup> findAll() {
        return MapstructUtils.convert(deviceGroupService.list(), DeviceGroup.class);
    }


}
