package cc.iotkit.manager.dto.bo.ota;

import cc.iotkit.common.api.BaseDto;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author: 石恒
 * @Date: 2023/6/16 21:13
 * @Description:
 */
@ApiModel(value = "DeviceUpgradeBo")
@Data
@EqualsAndHashCode(callSuper = true)
public class DeviceUpgradeBo extends BaseDto {
    private static final long serialVersionUID = -1L;
    private List<String> deviceIds;
    private Long otaId;
}
