package cc.iotkit.data.model;

import cc.iotkit.model.plugin.PluginInstance;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@ApiModel(value = "插件实例")
@Table(name = "plugin_instance")
@AutoMapper(target = PluginInstance.class)
public class TbPluginInstance extends BaseEntity {

    @Id
    @GeneratedValue(generator = "SnowflakeIdGenerator")
    @GenericGenerator(name = "SnowflakeIdGenerator", strategy = "cc.iotkit.data.config.id.SnowflakeIdGenerator")
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 插件主程序id
     */
    @ApiModelProperty(value = "插件主程序id")
    private String mainId;

    /**
     * 插件id
     */
    @ApiModelProperty(value = "插件id")
    private Long pluginId;

    /**
     * 插件主程序所在ip
     */
    @ApiModelProperty(value = "插件主程序所在ip")
    private String ip;

    /**
     * 插件主程序端口
     */
    @ApiModelProperty(value = "插件主程序端口")
    private int port;

    /**
     * 心跳时间
     * 心路时间超过30秒需要剔除
     */
    @ApiModelProperty(value = "心跳时间")
    private Long heartbeatAt;

}
