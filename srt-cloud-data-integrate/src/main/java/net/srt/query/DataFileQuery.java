package net.srt.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.srt.framework.common.query.Query;

import java.util.Date;

/**
* 文件表查询
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-11-16
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "文件表查询")
public class DataFileQuery extends Query {
    @Schema(description = "名称")
    private String name;
    @Schema(description = "文件类型")
    private String type;
	@Schema(description = "文件分组id")
    private Long fileCategoryId;
	private Long resourceId;
	private Integer queryApply;

}
