package net.srt.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.srt.framework.common.query.Query;

import java.util.Date;

/**
* 数据项目查询
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-09-27
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "数据项目查询")
public class DataProjectQuery extends Query {
    @Schema(description = "项目名称")
    private String name;

    @Schema(description = "英文名称")
    private String engName;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "负责人")
    private String dutyPerson;

}
