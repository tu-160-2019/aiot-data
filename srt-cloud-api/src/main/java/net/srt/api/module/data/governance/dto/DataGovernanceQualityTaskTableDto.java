package net.srt.api.module.data.governance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.srt.api.module.data.governance.dto.quality.QulaityColumn;
import net.srt.framework.common.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 数据治理-表检测记录
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2023-06-23
 */
@Data
@Schema(description = "数据治理-表检测记录")
public class DataGovernanceQualityTaskTableDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键id")
	private Long id;

	@Schema(description = "质量任务id")
	private Long qualityTaskId;

	@Schema(description = "被检测的表id")
	private Long tableMetadataId;

	@Schema(description = "被检测的表")
	private String tableName;

	@Schema(description = "被检测的字段id")
	private List<QulaityColumn> columnInfo;

	@Schema(description = "检测条数")
	private Integer checkCount = 0;

	@Schema(description = "检测通过数")
	private Integer passCount = 0;

	@Schema(description = "未通过数")
	private Integer notPassCount = 0;

	@Schema(description = "检测时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date checkTime;
	@Schema(description = "开始时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date startTime;
	@Schema(description = "结束时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date endTime;
	private String errorLog;

	@Schema(description = "运行状态（ 1-等待中 2-运行中 3-正常结束 4-异常结束）")
	private Integer status;

	@Schema(description = "项目id")
	private Long projectId;

	@Schema(description = "版本号")
	private Integer version;

	@Schema(description = "删除标识  0：正常   1：已删除")
	private Integer deleted;

	@Schema(description = "创建者")
	private Long creator;

	@Schema(description = "创建时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date createTime;

	@Schema(description = "更新者")
	private Long updater;

	@Schema(description = "更新时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date updateTime;


}
