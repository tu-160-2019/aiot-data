package net.srt.api.module.data.integrate.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.srt.framework.common.utils.DateUtils;
import srt.cloud.framework.dbswitch.data.config.DbswichProperties;

import java.io.Serializable;
import java.util.Date;

/**
* 数据集成-数据接入
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-10-24
*/
@Data
@Schema(description = "数据集成-数据接入")
public class DataAccessDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键id")
	private Long id;

	@Schema(description = "任务名称")
	private String taskName;

	@Schema(description = "描述")
	private String description;

	@Schema(description = "项目id")
	private Long projectId;
	private Long orgId;

	private Integer sourceType;

	@Schema(description = "源端数据库id")
	private Long sourceDatabaseId;

	@Schema(description = "目的端数据库id")
	private Long targetDatabaseId;

	@Schema(description = "接入方式 1-ods接入 2-自定义接入")
	private Integer accessMode;

	@Schema(description = "任务类型")
	private Integer taskType;

	@Schema(description = "cron表达式")
	private String cron;

	@Schema(description = "发布状态")
	private Integer status;

	@Schema(description = "最新运行状态")
	private Integer runStatus;

	@Schema(description = "数据接入基础配置json")
	private DbswichProperties dataAccessJson;

	@Schema(description = "最近开始时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date startTime;

	@Schema(description = "最近结束时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date endTime;

	@Schema(description = "发布时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date releaseTime;

	@Schema(description = "备注")
	private String note;

	@Schema(description = "发布人id")
	private Long releaseUserId;

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
