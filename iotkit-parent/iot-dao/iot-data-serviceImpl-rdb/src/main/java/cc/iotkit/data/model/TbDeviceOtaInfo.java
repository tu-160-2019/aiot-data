package cc.iotkit.data.model;

import cc.iotkit.model.ota.DeviceOtaInfo;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @Author: 石恒
 * @Date: 2023/6/15 22:22
 * @Description:
 */
@Data
@Entity
@Table(name = "device_ota_info")
@ApiModel(value = "设备信息")
@AutoMapper(target = DeviceOtaInfo.class)
public class TbDeviceOtaInfo {
    @Id
    @GeneratedValue(generator = "SnowflakeIdGenerator")
    @GenericGenerator(name = "SnowflakeIdGenerator", strategy = "cc.iotkit.data.config.id.SnowflakeIdGenerator")
    private Long id;

    private Long packageId;

    @Column(name = "[desc]")
    private String desc;

    private String version;

    private String module;

    private Integer total;

    private Integer success;

    private Integer fail;

    private String productKey;

    private Long createAt;
}
