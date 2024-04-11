package net.srt.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.srt.framework.common.query.Query;

import java.util.Date;

/**
* 数据集成-贴源数据查询
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-11-07
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "数据集成-贴源数据查询")
public class DataTableQuery extends Query {
    @Schema(description = "表名")
    private String tableName;

    @Schema(description = "注释")
    private String remarks;

    @Schema(description = "项目id")
    private Long projectId;

}
