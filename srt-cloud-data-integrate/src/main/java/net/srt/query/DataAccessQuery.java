package net.srt.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.srt.framework.common.query.Query;

/**
* 数据集成-数据接入查询
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-10-24
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "数据集成-数据接入查询")
public class DataAccessQuery extends Query {
    @Schema(description = "任务名称")
    private String taskName;

    @Schema(description = "项目id")
    private Integer projectId;

    @Schema(description = "数据库id")
    private Integer dataDatabaseId;

    @Schema(description = "发布状态")
    private Integer status;

    @Schema(description = "最新运行状态")
    private Integer runStatus;

}
