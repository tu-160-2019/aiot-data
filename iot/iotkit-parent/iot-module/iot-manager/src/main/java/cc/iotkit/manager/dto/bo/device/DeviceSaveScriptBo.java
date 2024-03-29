package cc.iotkit.manager.dto.bo.device;

import cc.iotkit.common.api.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;


@ApiModel(value = "DeviceSaveScriptBo")
@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceSaveScriptBo extends BaseDto {

    private static final long serialVersionUID = -1L;

    @NotBlank(message = "id不能为空")
    @ApiModelProperty(value = "id")
    private String id;


    @ApiModelProperty(value = "脚本")
    private String script;

}
