package net.srt.api.module.data.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.srt.framework.common.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
* 数据服务-权限关联表
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2023-02-16
*/
@Data
@Schema(description = "数据服务-权限关联表")
public class DataServiceApiAuthDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键id")
	private Long id;

	@Schema(description = "app的id")
	private Long appId;

	@Schema(description = "分组id")
	private Long groupId;

	@Schema(description = "api的id")
	private Long apiId;

	@Schema(description = "调用次数 不限次数为-1")
	private Integer requestTimes;

	@Schema(description = "已调用次数")
	private Integer requestedTimes;
	private Integer requestedSuccessTimes;
	private Integer requestedFailedTimes;

	@Schema(description = "所属项目id")
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

	private Date startTime;
	private Date endTime;

	private Boolean hasActiveApply;


}
