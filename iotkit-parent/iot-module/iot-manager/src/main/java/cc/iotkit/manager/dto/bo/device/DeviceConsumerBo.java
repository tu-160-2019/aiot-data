package cc.iotkit.manager.dto.bo.device;

import cc.iotkit.common.api.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;


@ApiModel(value = "DeviceConsumerBo")
@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceConsumerBo extends BaseDto {

    private static final long serialVersionUID = -1L;

    @NotBlank(message = "deviceId不能为空")
    @ApiModelProperty(value = "设备id")
    private String deviceId;

    @NotBlank(message = "clientId不能为空")
    @ApiModelProperty(value = "clientId")
    private String clientId;

}
