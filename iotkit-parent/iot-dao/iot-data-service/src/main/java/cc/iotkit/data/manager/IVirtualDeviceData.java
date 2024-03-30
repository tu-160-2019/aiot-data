/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.data.manager;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.data.IOwnedData;
import cc.iotkit.model.alert.AlertConfig;
import cc.iotkit.model.device.VirtualDevice;

import java.util.List;

public interface IVirtualDeviceData extends IOwnedData<VirtualDevice, String> {

    Paging<VirtualDevice> findByUid(String uid, int size, int page);

    List<VirtualDevice> findByUidAndState(String uid, String state);

    List<VirtualDevice> findByTriggerAndState(String trigger, String state);
    
}
