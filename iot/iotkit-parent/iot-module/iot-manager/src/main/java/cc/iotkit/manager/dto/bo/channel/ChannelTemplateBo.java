package cc.iotkit.manager.dto.bo.channel;

import cc.iotkit.common.api.BaseDto;
import cc.iotkit.model.notify.ChannelTemplate;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(value = "ChannelTemplateVo")
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ChannelTemplate.class, reverseConvertGenerate = false)
public class ChannelTemplateBo extends BaseDto {

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
