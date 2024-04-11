package net.srt.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.srt.framework.common.query.Query;

/**
* 数据接入-同步记录详情查询
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-10-28
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "数据接入-同步记录详情查询")
public class DataAccessTaskDetailQuery extends Query {
    @Schema(description = "是否成功 0-否 1-是")
    private Integer ifSuccess;
    private Long taskId;
    private Long projectId;
    private String tableName;

}
