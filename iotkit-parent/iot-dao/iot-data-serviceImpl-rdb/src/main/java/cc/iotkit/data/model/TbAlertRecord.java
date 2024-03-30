package cc.iotkit.data.model;

import cc.iotkit.model.alert.AlertRecord;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "alert_record")
@AutoMapper(target = AlertRecord.class)
public class TbAlertRecord {

    @Id
    @GeneratedValue(generator = "SnowflakeIdGenerator")
    @GenericGenerator(name = "SnowflakeIdGenerator", strategy = "cc.iotkit.data.config.id.SnowflakeIdGenerator")
    @ApiModelProperty(value = "告警记录id")
    private Long id;

    /**
     * 配置所属用户
     */
    @ApiModelProperty(value = "配置所属用户")
    private String uid;

    /**
     * 告警名称
     */
    @ApiModelProperty(value = "告警名称")
    private String name;

    /**
     * 告警严重度（1-5）
     */
    @ApiModelProperty(value = "告警严重度（1-5）")
    private String level;

    /**
     * 告警时间
     */
    @ApiModelProperty(value = "告警时间")
    private Long alertTime;

    /**
     * 告警详情
     */
    @ApiModelProperty(value = "告警详情")
    private String details;

    /**
     * 是否已读
     */
    @ApiModelProperty(value = "是否已读")
    private Boolean readFlg;

}
