package net.srt.api.module.data.integrate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.srt.framework.common.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
* 数据集成-贴源数据
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-11-07
*/
@Data
@Schema(description = "数据集成-贴源数据")
public class DataTableDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键id")
	private Long id;

	@Schema(description = "数据接入id")
	private Long dataAccessId;
	private String layer;
	private Integer ifMaster;
	@Schema(description = "表名")
	private String tableName;

	@Schema(description = "注释")
	private String remarks;

	@Schema(description = "项目id")
	private Long projectId;

	@Schema(description = "最近同步时间")
	@JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
	private Date recentlySyncTime;

	@Schema(description = "版本号")
	private Integer version;

	@Schema(description = "删除标识  0：正常  1：已删除")
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
