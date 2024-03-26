package net.srt.api.module.data.governance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.srt.framework.common.utils.DateUtils;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;
import srt.cloud.framework.dbswitch.core.model.ColumnMetaData;

import java.io.Serializable;
import java.util.Date;

/**
 * 数据治理-主数据模型字段
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2023-09-27
 */
@Data
@Schema(description = "数据治理-主数据模型字段")
@AllArgsConstructor
@NoArgsConstructor
public class DataGovernanceMasterColumnDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键id")
	private Long id;

	@Schema(description = "模型id")
	private Long masterModelId;

	@Schema(description = "字段名称")
	private String name;

	@Schema(description = "注释")
	private String comment;

	@Schema(description = "数据类型")
	private Integer fieldType;

	@Schema(description = "长度")
	private Integer fieldLength;

	@Schema(description = "小数位数")
	private Integer sacle;

	@Schema(description = "是否可为空 0-否 1-是")
	private Integer nullable;

	@Schema(description = "主键 0-否 1-是")
	private Integer pk;

	@Schema(description = "版本号")
	private Integer version;

	@Schema(description = "删除标识  0：正常   1：已删除")
	private Integer deleted;

	private Long projectId;

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
