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
import cc.iotkit.model.device.DeviceInfo;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(value = "DeviceTagAddBo")
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = DeviceInfo.Tag.class, reverseConvertGenerate = false)
public class DeviceTagAddBo extends BaseDto {


    @ApiModelProperty(value="设备")
    private String deviceId;


    @ApiModelProperty(value="tag id")

    private String id;
    @ApiModelProperty(value="tag名称")

    private String name;
    @ApiModelProperty(value="tag值")

    private String value;
}
