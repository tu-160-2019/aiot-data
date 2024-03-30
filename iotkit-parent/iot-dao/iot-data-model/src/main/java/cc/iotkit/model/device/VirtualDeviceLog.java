/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.model.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 虚拟设备日志
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VirtualDeviceLog {

    private String id;

    /**
     * 虚拟设备id
     */
    private String virtualDeviceId;

    /**
     * 虚拟设备名称
     */
    private String virtualDeviceName;

    /**
     * 关联设备数量
     */
    private int deviceTotal;

    /**
     * 虚拟设备执行结果
     */
    private String result;

    /**
     * 创建时间
     */
    private Long logAt = System.currentTimeMillis();
}
