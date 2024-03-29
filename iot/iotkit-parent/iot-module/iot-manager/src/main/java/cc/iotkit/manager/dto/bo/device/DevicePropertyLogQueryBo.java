/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.manager.dto.bo.device;

import cc.iotkit.common.api.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(value = "DevicePropertiesLogQueryBo")
@Data
@EqualsAndHashCode(callSuper = true)
public class DevicePropertyLogQueryBo extends BaseDto {


    @ApiModelProperty(value="设备id")
    private String deviceId;
    @ApiModelProperty(value="属性名称")
    private String name;
    @ApiModelProperty(value="开始时间")
    private long start;
    @ApiModelProperty(value="结束时间")
    private long end;
}
