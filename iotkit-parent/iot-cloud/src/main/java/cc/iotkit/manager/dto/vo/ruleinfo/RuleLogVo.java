package cc.iotkit.manager.dto.vo.ruleinfo;

import cc.iotkit.model.rule.RuleLog;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "RuleLogVo")
@Data
@AutoMapper(target = RuleLog.class)
public class RuleLogVo implements Serializable {
    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value = "时间")
    private Long logAt;

    @ApiModelProperty(value = "规则id")
    private String ruleId;

    @ApiModelProperty(value = "状态")
    private String state;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "是否成功")
    private Boolean success;

}
