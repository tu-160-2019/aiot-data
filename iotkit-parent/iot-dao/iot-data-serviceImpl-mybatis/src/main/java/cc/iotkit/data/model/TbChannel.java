package cc.iotkit.data.model;

import cc.iotkit.model.notify.Channel;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@Data
@TableName("channel")
@AutoMapper(target= Channel.class)
public class TbChannel {
    @TableId(value = "id", type = IdType.ASSIGN_ID)
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
