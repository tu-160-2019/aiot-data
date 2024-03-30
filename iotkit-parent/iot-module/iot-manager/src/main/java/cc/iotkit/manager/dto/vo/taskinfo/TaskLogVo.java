/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.manager.dto.vo.taskinfo;

import cc.iotkit.model.rule.TaskLog;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;


@ApiModel(value = "TaskLogVo")
@Data
@AutoMapper(target = TaskLog.class)
public class TaskLogVo implements Serializable {
    private static final long serialVersionUID = -1L;

    private String id;

    private String taskId;

    private String content;

    private Boolean success;

    private Long logAt;
}
