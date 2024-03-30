package cc.iotkit.data.model;

import cc.iotkit.model.device.DeviceConfig;
import io.github.linpeilie.annotations.AutoMapper;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@ApiModel(value = "设备配置")
@Table(name = "device_config")
@AutoMapper(target = DeviceConfig.class)
public class TbDeviceConfig {

    @Id
    @GeneratedValue(generator = "SnowflakeIdGenerator")
    @GenericGenerator(name = "SnowflakeIdGenerator", strategy = "cc.iotkit.data.config.id.SnowflakeIdGenerator")
    @ApiModelProperty(value = "设备配置id")
    private String id;

    @ApiModelProperty(value = "设备id")
    private String deviceId;

    /**
     * 产品key
     */
    @ApiModelProperty(value = "产品key")
    private String productKey;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 设备配置json内容
     */
    @Column(columnDefinition = "text")
    @ApiModelProperty(value = "设备配置json内容")
    private String config;

    @ApiModelProperty(value = "创建时间")
    private Long createAt;

}
