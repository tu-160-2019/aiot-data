package cc.iotkit.model.ota;

import cc.iotkit.model.Id;
import lombok.*;

/**
 * @Author: 石恒
 * @Date: 2023/6/15 22:00
 * @Description:
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceOtaInfo implements Id<Long> {

    private Long id;

    private Long packageId;

    private String desc;

    private String version;

    private String module;

    private Integer total;

    private Integer success;

    private Integer fail;

    private String productKey;

    private Long createAt;

}
