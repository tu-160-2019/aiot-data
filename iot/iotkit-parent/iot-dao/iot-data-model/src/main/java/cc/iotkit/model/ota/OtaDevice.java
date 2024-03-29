package cc.iotkit.model.ota;

import cc.iotkit.model.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: 石恒
 * @Date: 2023/6/10 14:36
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtaDevice implements Id<Long> {

    private Long id;

    private String deviceName;

    private String deviceId;

    private String version;

    private Integer status;

    private Long createAt;
}

