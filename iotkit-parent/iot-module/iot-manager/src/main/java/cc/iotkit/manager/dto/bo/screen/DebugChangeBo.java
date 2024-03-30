package cc.iotkit.manager.dto.bo.screen;

import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Author：tfd
 * @Date：2023/6/26 11:23
 */
@Data
public class DebugChangeBo {

    private static final long serialVersionUID = -1L;

    @NotBlank(message = "id不能为空")
    @ApiModelProperty(value = "id")
    private Long id;

    @NotBlank(message = "转换状态不能为空")
    @ApiModelProperty(value = "转换状态")
    private Boolean state;
}
