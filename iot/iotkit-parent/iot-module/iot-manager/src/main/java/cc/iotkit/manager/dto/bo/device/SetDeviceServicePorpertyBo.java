package cc.iotkit.manager.dto.bo.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * @author: regan
 * @description:
 * @date:created in 2023/6/17 12:17
 * @modificed by:
 */
@ApiModel(value = "SetDeviceServicePorpertyBo")
@Data
public class SetDeviceServicePorpertyBo {
    @ApiModelProperty(value="设备id",required = true)
    @NotBlank
    private String deviceId;
    @ApiModelProperty(value="参数")
    private Map<String, Object> args;

}
