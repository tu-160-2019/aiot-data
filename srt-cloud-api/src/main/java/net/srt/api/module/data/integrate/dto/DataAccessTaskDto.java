package net.srt.api.module.data.integrate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import net.srt.framework.common.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
* 数据接入任务记录
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-10-26
*/
@Data
@Schema(description = "数据接入任务记录")
public class DataAccessTaskDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键id")
	private Long id;

	@Schema(description = "数据接入任务id")
	private Long dataAccessId;

	@Schema(description = "运行状态（ 1-等待中 2-运行中 3-正常结束 4-异常结束）")
	private Integer runStatus;

	@Schema(description = "开始时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date startTime;

	@Schema(description = "结束时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date endTime;

	private String realTimeLog;
	@Schema(description = "错误信息")
	private String errorInfo;

	@Schema(description = "更新数据量")
	private Long dataCount;

	@Schema(description = "成功表数量")
	private Long tableSuccessCount;

	@Schema(description = "失败表数量")
	private Long tableFailCount;

	@Schema(description = "更新大小")
	private String byteCount;

	@Schema(description = "项目id")
	private Long projectId;

	private Long orgId;

	//1-自动调度 2-手动触发
	private Integer runType;
	private String executeNo;


	@Schema(description = "创建时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date createTime;

	@Schema(description = "版本号")
	private Integer version;

	@Schema(description = "删除标识  0：正常   1：已删除")
	private Integer deleted;

	@Schema(description = "创建者")
	private Long creator;

	@Schema(description = "更新者")
	private Long updater;

	@Schema(description = "更新时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date updateTime;

	private Date nextRunTime;

	private Boolean updateTaskAccess;


}
