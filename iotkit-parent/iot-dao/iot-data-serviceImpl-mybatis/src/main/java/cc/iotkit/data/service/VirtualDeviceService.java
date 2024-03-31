package cc.iotkit.data.service;

import cc.iotkit.data.model.TbVirtualDevice;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface VirtualDeviceService extends IService<TbVirtualDevice> {
    List<TbVirtualDevice> findByTriggerAndState(String trigger, String state);

    Page<TbVirtualDevice> findByUid(String uid, int size, int page);
}
