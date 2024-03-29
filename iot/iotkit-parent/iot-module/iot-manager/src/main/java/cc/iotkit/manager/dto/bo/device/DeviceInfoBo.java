package cc.iotkit.manager.dto.bo.device;

import cc.iotkit.common.api.BaseDto;
import cc.iotkit.model.device.DeviceInfo;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import io.github.linpeilie.annotations.ReverseAutoMapping;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;


@ApiModel(value = "DeviceInfoBo")
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = DeviceInfo.class, reverseConvertGenerate = false)
public class DeviceInfoBo extends BaseDto {

    private static final long serialVersionUID = -1L;

    private String id;

    @ApiModelProperty(value = "创建时间")
    private Long createAt;

    @ApiModelProperty(value = "设备id")
    @Size(max = 255, message = "设备id长度不正确")
    private String deviceId;

    @ApiModelProperty(value = "设备名称")
    @Size(max = 255, message = "设备名称长度不正确")
    private String deviceName;

    @ApiModelProperty(value = "设备类型")
    @Size(max = 255, message = "设备类型长度不正确")
    private String model;

    @ApiModelProperty(value = "设备离线时间")
    private Long offlineTime;

    @ApiModelProperty(value = "设备在线时间")
    private Long onlineTime;

    @ApiModelProperty(value = "父级id")
    @Size(max = 255, message = "父级id长度不正确")
    private String parentId;

    @ApiModelProperty(value = "产品key")
    @Size(max = 255, message = "产品key长度不正确")
    private String productKey;

    @ApiModelProperty(value = "设备描述")
    @Size(max = 255, message = "设备描述长度不正确")
    private String secret;

    @ApiModelProperty(value = "经度")
    private String longitude;

    @ApiModelProperty(value = "纬度")
    private String latitude;

    @ApiModelProperty(value = "用户id")
    @Size(max = 255, message = "用户id长度不正确")
    private String uid;

}
