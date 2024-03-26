package net.srt.api.module.data.governance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.srt.framework.common.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
* 数据治理-元数据属性值
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2023-03-29
*/
@Data
@Schema(description = "数据治理-元数据属性值")
public class DataGovernanceMetadataPropertyDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键id")
	private Long id;

	@Schema(description = "属性id")
	private Long metamodelPropertyId;

	@Schema(description = "元数据id")
	private Long metadataId;

	@Schema(description = "属性值")
	private String property;

	@Schema(description = "项目id（租户id）")
	private Long projectId;

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
