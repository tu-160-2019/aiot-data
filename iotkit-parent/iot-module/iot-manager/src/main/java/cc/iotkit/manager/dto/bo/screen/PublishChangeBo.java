package cc.iotkit.manager.dto.bo.screen;

import cc.iotkit.common.api.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;


@ApiModel(value = "ChangeStateBo")
@Data
@EqualsAndHashCode(callSuper = true)
public class PublishChangeBo extends BaseDto {
    private static final long serialVersionUID = -1L;

    @NotBlank(message = "id不能为空")
    @ApiModelProperty(value = "")
    private Long id;


    @NotBlank(message = "state不能为空")
    @ApiModelProperty(value = "运行状态")
    @Size(max = 255, message = "运行状态长度不正确")
    private String state;


}
