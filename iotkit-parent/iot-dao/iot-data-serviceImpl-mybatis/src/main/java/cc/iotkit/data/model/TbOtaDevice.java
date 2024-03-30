package cc.iotkit.data.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Author: 石恒
 * @Date: 2023/5/25 23:26
 * @Description:
 */
@Data
@Entity
@Table(name = "ota_device")
public class TbOtaDevice {

    @Id
    @GeneratedValue(generator = "SnowflakeIdGenerator")
    @GenericGenerator(name = "SnowflakeIdGenerator", strategy = "cc.iotkit.data.config.id.SnowflakeIdGenerator")
    private Long id;

    private String deviceName;

    private String deviceId;

    private String version;

    private Integer status;

    private Long createAt;
}
