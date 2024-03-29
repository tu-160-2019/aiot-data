package cc.iotkit.manager.dto.bo.deviceconfig;

import cc.iotkit.common.api.BaseDto;
import cc.iotkit.model.device.DeviceConfig;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;


@ApiModel(value = "DeviceConfigBo")
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = DeviceConfig.class, reverseConvertGenerate = false)
public class DeviceConfigBo extends BaseDto {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value = "设备配置json内容")
    @Size(max = 65535, message = "设备配置json内容长度不正确")
    private String config;

    @ApiModelProperty(value = "创建时间")
    private Long createAt;

    @ApiModelProperty(value = "设备id")
    @Size(max = 255, message = "设备id长度不正确")
    private String deviceId;

    @ApiModelProperty(value = "设备名称")
    @Size(max = 255, message = "设备名称长度不正确")
    private String deviceName;

    @ApiModelProperty(value = "产品key")
    @Size(max = 255, message = "产品key长度不正确")
    private String productKey;

}
