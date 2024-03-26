package net.srt.api.module.data.governance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.srt.framework.common.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
* 数据治理-主数据模型
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2023-09-27
*/
@Data
@Schema(description = "数据治理-主数据模型")
public class DataGovernanceMasterModelDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键id")
	private Long id;

	@Schema(description = "主数据目录id")
	private Long catalogId;

	@Schema(description = "表名")
	private String tableName;

	@Schema(description = "中文名称")
	private String tableCn;

	@Schema(description = "备注")
	private String description;

	@Schema(description = "发布状态 0-未发布 1-已发布")
	private Integer status;

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

	private List<DataGovernanceMasterColumnDto> columns;

}
