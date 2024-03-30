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
import io.github.linpeilie.annotations.AutoMapping;
import io.github.linpeilie.annotations.ReverseAutoMapping;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(value = "DeviceQueryBo")
@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceQueryBo extends BaseDto {


    @ApiModelProperty(value="关键字")
    private String keyword;

    @ApiModelProperty(value="分组")
    @AutoMapping(ignore = true)
    @ReverseAutoMapping(ignore = true)
    private String group;

    @ApiModelProperty(value="设备id")
    @Size(max = 255, message = "设备id长度不正确")
    private String deviceId;

    @ApiModelProperty(value="设备名称")
    @Size(max = 255, message = "设备名称长度不正确")
    private String deviceName;

    @ApiModelProperty(value="设备类型")
    @Size(max = 255, message = "设备类型长度不正确")
    private String model;

    @ApiModelProperty(value="父级id")
    @Size(max = 255, message = "父级id长度不正确")
    private String parentId;

    @ApiModelProperty(value="产品key")
    @Size(max = 255, message = "产品key长度不正确")
    private String productKey;


    @ApiModelProperty(value="设备状态")
    @Size(max = 255, message = "设备状态长度不正确")
    @AutoMapping(ignore = true)
    @ReverseAutoMapping(ignore = true)
    private Boolean online;

    @ApiModelProperty(value="用户id")
    @Size(max = 255, message = "用户id长度不正确")
    private String uid;

}
