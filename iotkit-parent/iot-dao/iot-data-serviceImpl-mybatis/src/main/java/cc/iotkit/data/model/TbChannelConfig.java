package cc.iotkit.data.model;

import cc.iotkit.model.notify.ChannelConfig;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import org.apache.ibatis.type.JdbcType;


/**
 * @Author: 石恒
 * @Date: 2023/5/11 20:58
 * @Description:
 */
@Data
@TableName("channel_config")
@ApiModel(value = "通道配置")
@AutoMapper(target = ChannelConfig.class)
public class TbChannelConfig {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "通道配置id")
    private Long id;

    @ApiModelProperty(value = "通道id")
    private Long channelId;

    @ApiModelProperty(value = "通道配置名称")
    private String title;

    @ApiModelProperty(value = "通道配置参数")
    @TableField(jdbcType = JdbcType.LONGVARCHAR)
    private String param;

    @ApiModelProperty(value = "创建时间")
    private Long createAt;
}
