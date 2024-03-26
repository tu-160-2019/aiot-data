package net.srt.api.module.data.governance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.srt.framework.common.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
* 数据治理-字段检测记录
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2023-06-23
*/
@Data
@Schema(description = "数据治理-字段检测记录")
public class DataGovernanceQualityTaskColumnDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键id")
	private Long id;

	@Schema(description = "质量任务id")
	private Long qualityTaskId;

	@Schema(description = "表检测记录id")
	private Long qualityTaskTableId;

	@Schema(description = "被检测的数据行")
	private String checkRow;

	@Schema(description = "未通过详情")
	private String notPassInfo;

	@Schema(description = "检测时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date checkTime;

	@Schema(description = "0-不通过 1-通过")
	private Integer checkResult;

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

	private Integer notPassReason;


}
