package cc.iotkit.data.model;

import cc.iotkit.model.notify.ChannelTemplate;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @Author: 石恒
 * @Date: 2023/5/11 20:59
 * @Description:
 */
@Data
@TableName("channel_template")
@ApiModel(value = "通道模板")
@AutoMapper(target= ChannelTemplate.class)
public class TbChannelTemplate {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "通道模板id")
    private Long id;

    @ApiModelProperty(value = "通道配置id")
    private Long channelConfigId;

    @ApiModelProperty(value = "通道模板名称")
    private String title;

    @ApiModelProperty(value = "通道模板内容")
    private String content;

    @ApiModelProperty(value = "创建时间")
    private Long createAt;
}
