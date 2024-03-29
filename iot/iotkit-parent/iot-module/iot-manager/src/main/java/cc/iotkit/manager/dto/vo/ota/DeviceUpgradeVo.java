package cc.iotkit.manager.dto.vo.ota;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 石恒
 * @Date: 2023/7/18 21:48
 * @Description:
 */
@Data
@Builder
public class DeviceUpgradeVo implements Serializable {
    private String result;
}
