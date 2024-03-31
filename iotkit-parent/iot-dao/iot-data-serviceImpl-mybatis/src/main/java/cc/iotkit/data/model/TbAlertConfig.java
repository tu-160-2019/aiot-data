package cc.iotkit.data.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import cc.iotkit.model.alert.AlertConfig;

@Data
@TableName("alert_config")
@AutoMapper(target = AlertConfig.class)
public class TbAlertConfig {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "告警配置id")
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
     * 告警严重度
     */
    @ApiModelProperty(value = "告警严重度")
    private String level;

    /**
     * 关联规则引擎ID
     */
    @ApiModelProperty(value = "关联规则引擎ID")
    private String ruleInfoId;

    /**
     * 关联消息转发模板ID
     */
    @ApiModelProperty(value = "关联消息转发模板ID")
    private String messageTemplateId;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 是否启用
     */
    @ApiModelProperty(value = "是否启用")
    private Boolean enable;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Long createAt;

}
