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
@ApiModel(value = "ServiceInvokeBo")
@Data
public class ServiceInvokeBo {
    @ApiModelProperty(value="设备id",required = true)
    @NotBlank
    private String deviceId;
    @ApiModelProperty(value="服务",required = true)
    @NotBlank
    private String service;
    @ApiModelProperty(value="参数")
    private Map<String, Object> args;

}
