package net.srt.api.module.data.governance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.srt.framework.common.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
* 数据治理-主数据派发日志
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2023-10-08
*/
@Data
@Schema(description = "数据治理-主数据派发日志")
public class DataGovernanceMasterDistributeLogDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键id")
	private Long id;

	@Schema(description = "派发id")
	private Long distributeId;

	@Schema(description = "主数据id")
	private Long masterModelId;

	@Schema(description = "运行状态（ 1-等待中 2-运行中 3-正常结束 4-异常结束）")
	private Integer runStatus;

	@Schema(description = "开始时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date startTime;

	@Schema(description = "结束时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date endTime;

	@Schema(description = "错误信息")
	private String errorInfo;

	@Schema(description = "派发数据量")
	private Long dataCount;

	@Schema(description = "数据量大小")
	private String byteCount;

	@Schema(description = "项目id（租户id）")
	private Long projectId;
	private Long orgId;

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
