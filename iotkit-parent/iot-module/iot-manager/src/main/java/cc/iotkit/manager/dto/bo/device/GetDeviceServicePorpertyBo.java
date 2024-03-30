package cc.iotkit.manager.dto.bo.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * @author: regan
 * @description:
 * @date:created in 2023/6/17 12:17
 * @modificed by:
 */
@ApiModel(value = "GetDeviceServicePorpertyBo")
@Data
public class GetDeviceServicePorpertyBo {
    @ApiModelProperty(value="设备id",required = true)
    @NotBlank
    private String deviceId;

    @ApiModelProperty(value="属性列表")
    private List<String> propertyNames;

}
