package cc.iotkit.model.ota;

import lombok.Data;

/**
 * @Author: 石恒
 * @Date: 2023/6/10 14:36
 * @Description:
 */
@Data
public class DeviceOta {
    private String currentVersion;
    private String deviceId;
}