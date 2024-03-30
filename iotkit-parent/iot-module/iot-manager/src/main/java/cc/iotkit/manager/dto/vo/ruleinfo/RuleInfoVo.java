package cc.iotkit.manager.dto.vo.ruleinfo;

import cc.iotkit.model.rule.FilterConfig;
import cc.iotkit.model.rule.RuleAction;
import cc.iotkit.model.rule.RuleInfo;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@ApiModel(value = "RuleInfoVo")
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = RuleInfo.class)

public class RuleInfoVo implements Serializable {

    private static final long serialVersionUID = -1L;

    @ApiModelProperty(value = "规则id")
    @ExcelProperty(value = "规则id")
    private String id;

    @ApiModelProperty(value = "动作")
    @ExcelProperty(value = "动作")
    private List<RuleAction> actions;

    @ApiModelProperty(value = "创建时间")
    @ExcelProperty(value = "创建时间")
    private Long createAt;

    @ApiModelProperty(value = "描述")
    @ExcelProperty(value = "描述")
    private String desc;

    @ApiModelProperty(value = "过滤器")
    @ExcelProperty(value = "过滤器")
    private List<FilterConfig> filters;

    @ApiModelProperty(value = "监听器")
    @ExcelProperty(value = "监听器")
    private List<FilterConfig> listeners;

    @ApiModelProperty(value = "规则名称")
    @ExcelProperty(value = "规则名称")
    private String name;

    @ApiModelProperty(value = "状态")
    @ExcelProperty(value = "状态")
    private String state;

    @ApiModelProperty(value = "规则类型")
    @ExcelProperty(value = "规则类型")
    private String type;

    @ApiModelProperty(value = "用户id")
    @ExcelProperty(value = "用户id")
    private String uid;


}
