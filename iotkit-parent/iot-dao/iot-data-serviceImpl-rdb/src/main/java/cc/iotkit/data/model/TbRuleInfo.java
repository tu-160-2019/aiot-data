/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.data.model;

import cc.iotkit.model.rule.RuleInfo;
import io.github.linpeilie.annotations.AutoMapper;
import io.github.linpeilie.annotations.AutoMapping;
import io.github.linpeilie.annotations.ReverseAutoMapping;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@Entity
@Table(name = "rule_info")
@ApiModel(value = "规则")
@AutoMapper(target = RuleInfo.class)
public class TbRuleInfo {

    @Id
    @GeneratedValue(generator = "SnowflakeIdGenerator")
    @GenericGenerator(name = "SnowflakeIdGenerator", strategy = "cc.iotkit.data.config.id.SnowflakeIdGenerator")
    @ApiModelProperty(value = "规则id")
    private String id;

    @ApiModelProperty(value = "规则名称")
    private String name;

    @ApiModelProperty(value = "规则类型")
    private String type;

    @Column(columnDefinition = "text")
    @ApiModelProperty(value = "监听器")
    @AutoMapping(ignore = true)
    @ReverseAutoMapping(ignore = true)
    private String listeners;

    @Column(columnDefinition = "text")
    @AutoMapping(ignore = true)
    @ReverseAutoMapping(ignore = true)
    @ApiModelProperty(value = "过滤器")
    private String filters;

    @Column(columnDefinition = "text")
    @ApiModelProperty(value = "动作")
    @AutoMapping(ignore = true)
    @ReverseAutoMapping(ignore = true)
    private String actions;

    @ApiModelProperty(value = "用户id")
    private String uid;

    @ApiModelProperty(value = "状态")
    private String state;

    @Column(name = "[desc]")
    @ApiModelProperty(value = "描述")
    private String desc;

    @ApiModelProperty(value = "创建时间")
    private Long createAt;

}
