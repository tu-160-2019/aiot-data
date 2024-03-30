package cc.iotkit.data.model;

import cc.iotkit.model.notify.Channel;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * author: 石恒
 * date: 2023-05-11 17:53
 * description:
 **/
@Data
@Entity
@Table(name = "channel")
@AutoMapper(target= Channel.class)
public class TbChannel {
    @Id
    @GeneratedValue(generator = "SnowflakeIdGenerator")
    @GenericGenerator(name = "SnowflakeIdGenerator", strategy = "cc.iotkit.data.config.id.SnowflakeIdGenerator")
    @ApiModelProperty(value = "通道id")
    private Long id;

    @ApiModelProperty(value = "通道名称")
    private String code;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "图标")
    private String icon;

    @ApiModelProperty(value = "创建时间")
    private Long createAt;
}
