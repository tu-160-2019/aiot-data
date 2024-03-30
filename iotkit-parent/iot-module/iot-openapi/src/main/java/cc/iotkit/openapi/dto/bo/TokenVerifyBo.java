package cc.iotkit.openapi.dto.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@ApiModel(value = "TokenVerifyBo")
@Data
public class TokenVerifyBo {
    private static final long serialVersionUID = -1L;

    @NotBlank(message = "appid不能为空")
    @ApiModelProperty(value = "appid")
    private String appid;

    @NotBlank(message = "timestamp不能为空")
    @ApiModelProperty(value = "时间戳")
    private String timestamp;

    @NotBlank(message = "identifier不能为空")
    @ApiModelProperty(value = "标识符")
    private String identifier;

    @NotBlank(message = "{tenant.number.not.blank}")
    @ApiModelProperty(value = "租户ID")
    private String tenantId;

}
