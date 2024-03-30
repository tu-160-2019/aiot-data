package cc.iotkit.data.model;

import cc.iotkit.model.notify.ChannelConfig;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * @Author: 石恒
 * @Date: 2023/5/11 20:58
 * @Description:
 */
@Data
@Entity
@Table(name = "channel_config")
@ApiModel(value = "通道配置")
@AutoMapper(target = ChannelConfig.class)
public class TbChannelConfig {
    @Id
    @GeneratedValue(generator = "SnowflakeIdGenerator")
    @GenericGenerator(name = "SnowflakeIdGenerator", strategy = "cc.iotkit.data.config.id.SnowflakeIdGenerator")
    @ApiModelProperty(value = "通道配置id")
    private Long id;

    @ApiModelProperty(value = "通道id")
    private Long channelId;

    @ApiModelProperty(value = "通道配置名称")
    private String title;

    @ApiModelProperty(value = "通道配置参数")
    @Column(columnDefinition = "TEXT")
    private String param;

    @ApiModelProperty(value = "创建时间")
    private Long createAt;
}
