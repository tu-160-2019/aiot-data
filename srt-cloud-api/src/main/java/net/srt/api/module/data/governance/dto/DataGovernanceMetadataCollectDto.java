package net.srt.api.module.data.governance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.srt.framework.common.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
* 数据治理-元数据采集
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2023-04-01
*/
@Data
@Schema(description = "数据治理-元数据采集")
public class DataGovernanceMetadataCollectDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键id")
	private Long id;

	@Schema(description = "任务名称")
	private String name;

	@Schema(description = "数据库类型（1-数据库 2-中台库）")
	private Integer dbType;

	@Schema(description = "数据库主键id")
	private Long databaseId;

	@Schema(description = "入库策略，0-全量，1-增量")
	private Integer strategy;

	@Schema(description = "任务类型 1一次性 2.周期性")
	private Integer taskType;

	@Schema(description = "cron表达式（秒 分 时 日 月 星期 年，例如 0 0 3 * * ? 表示每天凌晨三点执行）")
	private String cron;

	@Schema(description = "归属元数据的目录")
	private Long metadataId;

	@Schema(description = "描述")
	private String description;

	@Schema(description = "是否已发布 0-否 1-是")
	private Integer status;

	@Schema(description = "发布时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date releaseTime;

	@Schema(description = "项目id")
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
