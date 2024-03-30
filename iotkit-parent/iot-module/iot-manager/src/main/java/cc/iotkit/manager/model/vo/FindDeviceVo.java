package cc.iotkit.manager.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindDeviceVo {

    private String deviceId;

    private String productKey;

    private String deviceName;

    private String productName;

    private String categoryName;

    private String productImg;

}
