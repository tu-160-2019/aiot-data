package cc.iotkit.manager.dto.bo.device;

import cc.iotkit.common.api.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;


@ApiModel(value = "DeviceQueryByPkDnBo")
@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceQueryByPkDnBo extends BaseDto {

    private static final long serialVersionUID = -1L;

    @NotBlank(message = "产品key不能为空")
    @ApiModelProperty(value = "产品key")
    private String pk;

    @NotBlank(message = "设备不能为空")
    @ApiModelProperty(value = "设备")
    private String dn;

}
