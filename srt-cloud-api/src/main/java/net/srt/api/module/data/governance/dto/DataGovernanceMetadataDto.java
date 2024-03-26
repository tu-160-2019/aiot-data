package net.srt.api.module.data.governance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.srt.framework.common.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
* 数据治理-元数据
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2023-03-29
*/
@Data
@Schema(description = "数据治理-元数据")
public class DataGovernanceMetadataDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键id")
	private Long id;

	@Schema(description = "父级id（默认0为顶级）")
	private Long parentId;

	@Schema(description = "树状节点的路径")
	private String path;

	@Schema(description = "节点名称")
	private String name;

	@Schema(description = "节点英文名称")
	private String code;

	private String icon;

	private Integer ifLeaf;

	@Schema(description = "对应的元模型id")
	private Long metamodelId;

	@Schema(description = "详情")
	private String description;

	@Schema(description = "数据库类型（1-数据库 2-中台库）")
	private Integer dbType;

	@Schema(description = "如果是外部系统接入的库表，需要此字段")
	private Long datasourceId;

	@Schema(description = "采集任务id")
	private Long collectTaskId;

	@Schema(description = "项目id（租户id）")
	private Long projectId;
	private Long orgId;

	private Integer orderNo;

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
