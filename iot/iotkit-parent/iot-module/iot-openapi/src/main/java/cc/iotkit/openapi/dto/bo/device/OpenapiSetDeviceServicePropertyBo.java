package cc.iotkit.openapi.dto.bo.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;
/**
 * @author: dsy
 * @description:
 * @date:created in 2023/7/25 14:17
 * @modificed by:
 */
@ApiModel(value = "OpenapiSetDeviceServicePropertyBo")
@Data
public class OpenapiSetDeviceServicePropertyBo {

    /*@ApiModelProperty(value="设备id",required = true)
    @NotBlank
    private String deviceId;*/

    @NotBlank(message = "deviceName不能为空")
    @ApiModelProperty(value = "产品名称")
    private String deviceName;

    @NotBlank(message = "productKey不能为空")
    @ApiModelProperty(value = "产品key")
    private String productKey;

    @ApiModelProperty(value="参数")
    private String args;
}
