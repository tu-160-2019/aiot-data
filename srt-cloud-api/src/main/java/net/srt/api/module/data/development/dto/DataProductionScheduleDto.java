package net.srt.api.module.data.development.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.srt.framework.common.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据生产-作业调度
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2023-01-12
 */
@Data
@Schema(description = "数据生产-作业调度")
public class DataProductionScheduleDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键id")
	private Integer id;

	private Long projectId;
	private Long orgId;

	@Schema(description = "调度名称")
	private String name;

	@Schema(description = "是否周期执行")
	private Integer ifCycle;

	@Schema(description = "cron表达式")
	private String cron;

	@Schema(description = "描述")
	private String note;

	@Schema(description = "节点关系json")
	private String edges;

	@Schema(description = "0-未发布 1-已发布")
	private Integer status;

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
