package cc.iotkit.data.service;

import cc.iotkit.data.model.TbVirtualDeviceMapping;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface VirtualDeviceMappingService extends IService<TbVirtualDeviceMapping> {
    void deleteByVirtualId(String virtualId);

    List<TbVirtualDeviceMapping> listByVirtualId(String virtualId);
}