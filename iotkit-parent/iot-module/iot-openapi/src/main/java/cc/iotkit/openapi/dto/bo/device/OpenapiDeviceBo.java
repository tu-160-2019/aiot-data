package cc.iotkit.openapi.dto.bo.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@ApiModel(value = "OpenapiDeviceBo")
@Data
public class OpenapiDeviceBo {
    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value = "父级ID")
    private String parentId;

    @NotBlank(message = "deviceName不能为空")
    @ApiModelProperty(value = "产品名称")
    private String deviceName;

    @NotBlank(message = "productKey不能为空")
    @ApiModelProperty(value = "产品key")
    private String productKey;
}
