package cc.iotkit.temporal;

import cc.iotkit.common.api.Paging;
import cc.iotkit.model.device.VirtualDeviceLog;

public interface IVirtualDeviceLogData {

    Paging<VirtualDeviceLog> findByVirtualDeviceId(String virtualDeviceId, int page, int size);

    void add(VirtualDeviceLog log);
}
