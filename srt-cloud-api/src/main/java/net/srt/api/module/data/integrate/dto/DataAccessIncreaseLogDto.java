package net.srt.api.module.data.integrate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.io.Serializable;
import net.srt.framework.common.utils.DateUtils;

import java.util.Date;

/**
* 数据集成-数据增量接入日志
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2024-02-26
*/
@Data
@Schema(description = "数据集成-数据增量接入日志")
public class DataAccessIncreaseLogDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键id")
	private Long id;

	@Schema(description = "数据接入id")
	private Long dataAccessId;

	@Schema(description = "源数据id")
	private Long sourceDatabaseId;

	@Schema(description = "目的端数据库id（同步方式为2有此值）")
	private Long targetDatabaseId;

	private String schemaName;

	@Schema(description = "表名")
	private String tableName;

	@Schema(description = "增量字段")
	private String increaseColumn;

	@Schema(description = "开始值")
	private String startVal;

	@Schema(description = "结束值")
	private String endVal;

	@Schema(description = "接入方式 1-接入ods 2-自定义接入")
	private Integer accessMode;

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
