package net.srt.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import net.srt.framework.common.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
* 数据接入-同步记录详情
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-10-28
*/
@Data
@Schema(description = "数据接入-同步记录详情")
public class DataAccessTaskDetailVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键id")
	private Long id;

	@Schema(description = "数据接入id")
	private Long dataAccessId;

	@Schema(description = "数据接入任务id")
	private Long taskId;

	@Schema(description = "源端库名")
	private String sourceSchemaName;

	@Schema(description = "源端表名")
	private String sourceTableName;

	@Schema(description = "目的端库名")
	private String targetSchemaName;

	@Schema(description = "目的端表名")
	private String targetTableName;

	@Schema(description = "同步记录数")
	private Long syncCount;

	@Schema(description = "同步数据量")
	private String syncBytes;

	@Schema(description = "是否成功 0-否 1-是")
	private Integer ifSuccess;

	@Schema(description = "失败信息")
	private String errorMsg;

	@Schema(description = "成功信息")
	private String successMsg;

	@Schema(description = "项目id")
	private Long projectId;

	private Long orgId;

	@Schema(description = "创建时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date createTime;


}
