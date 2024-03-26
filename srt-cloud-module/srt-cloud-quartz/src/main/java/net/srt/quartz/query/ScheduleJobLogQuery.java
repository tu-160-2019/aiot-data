package net.srt.quartz.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.srt.framework.common.query.Query;

/**
* 定时任务日志查询
*
* @author 阿沐 babamu@126.com
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "定时任务日志查询")
public class ScheduleJobLogQuery extends Query {
    @Schema(description = "任务id")
    private Long jobId;

    @Schema(description = "任务名称")
    private String jobName;

    @Schema(description = "任务组名")
    private String jobGroup;

}
