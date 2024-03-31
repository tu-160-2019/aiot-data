package cc.iotkit.data.service;

import cc.iotkit.common.api.Paging;
import cc.iotkit.data.model.TbDeviceGroup;
import com.baomidou.mybatisplus.extension.service.IService;

import cc.iotkit.model.device.DeviceGroup;

public interface DeviceGroupService extends IService<TbDeviceGroup> {

    Paging<DeviceGroup> findByNameLike(String name, int page, int size);

}
