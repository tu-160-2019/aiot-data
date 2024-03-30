package cc.iotkit.manager.dto.bo.virtualdevice;

import cc.iotkit.common.api.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@ApiModel(value = "VirtualSaveDevicesBo")
@Data
@EqualsAndHashCode(callSuper = true)
public class VirtualSaveDevicesBo extends BaseDto {

    private static final long serialVersionUID = -1L;

    @NotBlank(message = "id不能为空")
    @ApiModelProperty(value = "id")
    private String id;


    @ApiModelProperty(value = "设备id列表")
    private List<String> devices;

}
