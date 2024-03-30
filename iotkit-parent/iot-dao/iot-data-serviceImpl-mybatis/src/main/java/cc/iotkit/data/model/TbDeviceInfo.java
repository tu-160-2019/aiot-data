package cc.iotkit.data.model;

import cc.iotkit.model.device.DeviceInfo;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import io.github.linpeilie.annotations.ReverseAutoMapping;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "device_info")
@ApiModel(value = "设备信息")
@AutoMapper(target = DeviceInfo.class)
public class TbDeviceInfo {

    @Id
    @GeneratedValue(generator = "SnowflakeIdGenerator")
    @GenericGenerator(name = "SnowflakeIdGenerator", strategy = "cc.iotkit.data.config.id.SnowflakeIdGenerator")
    private String id;

    @ApiModelProperty(value = "设备id")
    private String deviceId;

    @ApiModelProperty(value = "产品key")
    private String productKey;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "设备类型")
    private String model;

    @ApiModelProperty(value = "设备密钥")
    private String secret;

    @ApiModelProperty(value = "父级id")
    private String parentId;

    @ApiModelProperty(value = "经度")
    private String longitude;

    @ApiModelProperty(value = "纬度")
    private String latitude;

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "设备状态")
    @AutoMapping(ignore = true)
    @ReverseAutoMapping(ignore = true)
    private String state;

    @ApiModelProperty(value = "设备在线时间")
    private Long onlineTime;

    @ApiModelProperty(value = "设备离线时间")
    private Long offlineTime;

    @ApiModelProperty(value = "创建时间")
    private Long createAt;

}
