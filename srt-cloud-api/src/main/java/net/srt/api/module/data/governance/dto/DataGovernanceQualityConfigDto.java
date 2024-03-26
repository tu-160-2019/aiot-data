package net.srt.api.module.data.governance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.srt.api.module.data.governance.dto.quality.QualityConfigParam;
import net.srt.framework.common.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
* 数据治理-质量规则配置
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2023-05-29
*/
@Data
@Schema(description = "数据治理-质量规则配置")
public class DataGovernanceQualityConfigDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "自增id")
	private Long id;

	private Long categoryId;

	@Schema(description = "名称")
	private String name;

	@Schema(description = "规则id")
	private Integer ruleId;

	@Schema(description = "个性化参数json")
	private QualityConfigParam param;
	@Schema(description = "当选择的规则类型为关联一致性的时候，返回此字段（前台用）")
	private String relMetadataStr;

	@Schema(description = "元数据字段列表")
	private List<Integer> metadataIds;
	@Schema(description = "检测的元数据字段信息字符串（前台用）")
	private String metadataStrs;

	@Schema(description = "状态，1-启用，0-关闭")
	private Integer status;

	@Schema(description = "任务类型，1-一次性任务，2-周期任务")
	private Integer taskType;

	@Schema(description = "cron表达式")
	private String cron;

	@Schema(description = "备注")
	private String note;

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
