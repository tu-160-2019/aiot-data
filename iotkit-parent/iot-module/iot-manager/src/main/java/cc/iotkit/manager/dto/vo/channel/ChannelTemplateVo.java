package cc.iotkit.manager.dto.vo.channel;

import cc.iotkit.model.notify.ChannelTemplate;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "ChannelTemplateVo")
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ChannelTemplate.class)
public class ChannelTemplateVo implements Serializable {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value="通道模板id")
    @ExcelProperty(value = "通道模板id")
    private Long id;

    @ApiModelProperty(value="通道配置id")
    @ExcelProperty(value = "通道配置id")
    private Long channelConfigId;

    @ApiModelProperty(value="通道模板名称")
    @ExcelProperty(value = "通道模板名称")
    private String title;

    @ApiModelProperty(value="通道模板内容")
    @ExcelProperty(value = "通道模板内容")
    private String content;

    @ApiModelProperty(value="创建时间")
    @ExcelProperty(value = "创建时间")
    private Long createAt;
}
