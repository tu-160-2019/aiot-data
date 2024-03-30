package cc.iotkit.data.model;

import cc.iotkit.model.notify.NotifyMessage;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @Author: 石恒
 * @Date: 2023/5/13 18:33
 * @Description:
 */
@Data
@Entity
@ApiModel(value = "通知消息")
@Table(name = "notify_message")
@AutoMapper(target= NotifyMessage.class)
public class TbNotifyMessage {
    @Id
    @GeneratedValue(generator = "SnowflakeIdGenerator")
    @GenericGenerator(name = "SnowflakeIdGenerator", strategy = "cc.iotkit.data.config.id.SnowflakeIdGenerator")
    @ApiModelProperty(value = "通知消息id")
    private Long id;

    private String content;

    private String messageType;

    private Boolean status;

    private Long createAt;

    private Long updateAt;
}
