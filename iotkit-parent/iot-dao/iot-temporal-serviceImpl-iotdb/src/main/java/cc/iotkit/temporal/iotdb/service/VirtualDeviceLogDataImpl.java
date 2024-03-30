/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.iotdb.service;

import cc.iotkit.common.api.Paging;
import cc.iotkit.model.device.VirtualDeviceLog;
import cc.iotkit.temporal.IVirtualDeviceLogData;
import org.springframework.stereotype.Service;

@Service
public class VirtualDeviceLogDataImpl implements IVirtualDeviceLogData {

    @Override
    public Paging<VirtualDeviceLog> findByVirtualDeviceId(String virtualDeviceId, int page, int size) {
       return new Paging<>();
    }

    @Override
    public void add(VirtualDeviceLog log) {
    }
}
