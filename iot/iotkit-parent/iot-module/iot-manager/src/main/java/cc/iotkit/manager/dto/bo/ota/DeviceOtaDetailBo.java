package cc.iotkit.manager.dto.bo.ota;

import cc.iotkit.common.api.BaseDto;
import cc.iotkit.model.ota.DeviceOtaDetail;
import cc.iotkit.model.ota.DeviceOtaInfo;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: 石恒
 * @Date: 2023/6/17 20:45
 * @Description:
 */
@Data
@ApiModel(value = "DeviceOtaDetailBo")
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = DeviceOtaDetail.class, reverseConvertGenerate = false)
public class DeviceOtaDetailBo extends BaseDto {

    private Long otaInfoId;

    private String version;

    private String deviceId;

    private String productKey;

    private String deviceName;
}
