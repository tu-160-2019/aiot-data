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
import cc.iotkit.manager.model.vo.DeviceLog;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(value = "DeviceLogQueryBo")
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = DeviceLog.class, reverseConvertGenerate = false)
public class DeviceLogQueryBo extends BaseDto {

    @ApiModelProperty(value="设备id")
    private String deviceId;

    @ApiModelProperty(value="类型")
    private String type;

    @ApiModelProperty(value="属性名")
    private String identifier;
}
