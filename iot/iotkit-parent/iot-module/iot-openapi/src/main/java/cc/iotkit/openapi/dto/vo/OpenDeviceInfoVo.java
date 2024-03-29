package cc.iotkit.openapi.dto.vo;

import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.product.ThingModel;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import lombok.Data;


@ApiModel(value = "OpenDeviceInfoVo")
@Data
@AutoMapper(target = DeviceInfo.class)
public class OpenDeviceInfoVo {
    private String id;

    private String deviceId;

    /**
     * 产品key
     */
    private String productKey;

    private String deviceName;

    /**
     * 设备型号
     */
    private String model;

    /**
     * 设备密钥
     */
    private String secret;

    private String parentId;

    /**
     * 所属平台用户ID
     */
    private String uid;

    private Long createAt;
}
