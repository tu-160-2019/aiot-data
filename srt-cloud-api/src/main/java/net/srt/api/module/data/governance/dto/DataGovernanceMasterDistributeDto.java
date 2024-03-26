package net.srt.api.module.data.governance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.srt.api.module.data.governance.dto.distribute.DistributeJson;
import net.srt.api.module.data.governance.dto.distribute.IncrField;
import net.srt.framework.common.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据治理-主数据派发
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2023-10-08
 */
@Data
@Schema(description = "数据治理-主数据派发")
public class DataGovernanceMasterDistributeDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键id")
	private Long id;

	private String name;

	@Schema(description = "主数据id")
	private Long masterModelId;

	@Schema(description = "派发类型 1-数据库 2-接口 3-消息队列")
	private Integer distributeType;

	@Schema(description = "派发json配置（不同类型json配置不同）")
	private DistributeJson distributeJson;

	@Schema(description = "上次执行到的增量记录")
	private IncrField incrField;

	@Schema(description = "状态 0-未发布 1-已发布")
	private Integer status;

	@Schema(description = "任务类型 2-一次性全量同步 3-一次性全量周期性增量")
	private Integer taskType;

	@Schema(description = "cron表达式")
	private String cron;

	@Schema(description = "描述")
	private String description;

	@Schema(description = "发布时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date releaseTime;

	@Schema(description = "发布人id")
	private Long releaseUserId;

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
