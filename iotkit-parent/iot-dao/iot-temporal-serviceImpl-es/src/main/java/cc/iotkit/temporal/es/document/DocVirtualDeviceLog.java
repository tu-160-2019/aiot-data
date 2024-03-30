/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.temporal.es.document;

import cc.iotkit.model.device.VirtualDeviceLog;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AutoMapper(target= VirtualDeviceLog.class)
@Document(indexName = "virtual_device_log")
public class DocVirtualDeviceLog {

    @Id
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
    @Field(type = FieldType.Date)
    private Long logAt = System.currentTimeMillis();
}
