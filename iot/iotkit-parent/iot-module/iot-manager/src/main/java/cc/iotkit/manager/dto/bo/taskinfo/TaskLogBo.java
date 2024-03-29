/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.manager.dto.bo.taskinfo;

import cc.iotkit.common.api.BaseDto;
import cc.iotkit.model.rule.TaskLog;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel(value = "TaskLogBo")
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = TaskLog.class, reverseConvertGenerate = false)
public class TaskLogBo extends BaseDto {

    private String id;

    private String taskId;

    private String content;

    private Boolean success;

    private Long logAt;
}
