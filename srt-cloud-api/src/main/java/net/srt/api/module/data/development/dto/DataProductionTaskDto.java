package net.srt.api.module.data.development.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.srt.framework.common.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
* 数据生产任务
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-12-05
*/
@Data
@Schema(description = "数据生产任务")
public class DataProductionTaskDto implements Serializable {
	private static final long serialVersionUID = 1L;

	@Schema(description = "主键id")
	private Long id;

	@Schema(description = "节点id")
	private Long catalogueId;

	@Schema(description = "任务名称")
	private String name;

	@Schema(description = "项目（租户id）")
	private Long projectId;
	private Long orgId;

	@Schema(description = "任务别名")
	private String alias;

	@Schema(description = "任务类型")
	private Integer dialect;

	@Schema(description = "任务运行类型")
	private Integer type;

	@Schema(description = "CheckPoint trigger seconds")
	private Integer checkPoint;

	@Schema(description = "SavePoint strategy")
	private Integer savePointStrategy;

	@Schema(description = "SavePointPath")
	private String savePointPath;

	@Schema(description = "并行度")
	private Integer parallelism;

	@Schema(description = "全局变量")
	private Boolean fragment;

	@Schema(description = "insrt 语句集")
	private Boolean statementSet;

	@Schema(description = "批处理模式")
	private Boolean batchModel;

	@Schema(description = "flink集群实例id")
	private Long clusterId;

	@Schema(description = "集群配置id")
	private Long clusterConfigurationId;

	@Schema(description = "数据类型（1-数据库 2-中台库）（sql模式下）")
	private Integer sqlDbType;

	@Schema(description = "数据库id（sql模式下）")
	private Long databaseId;

	@Schema(description = "Jar ID")
	private Long jarId;

	@Schema(description = "env id")
	private Long envId;

	@Schema(description = "alert group id")
	private Long alertGroupId;

	@Schema(description = "configuration json")
	private String configJson;

	@Schema(description = "Job Note")
	private String note;

	@Schema(description = "Job lifecycle")
	private Integer step;

	@Schema(description = "job instance id")
	private Long jobInstanceId;

	@Schema(description = "自动停止")
	private Boolean useAutoCancel;

	@Schema(description = "打印流")
	private Boolean useChangeLog;

	@Schema(description = "预览结果")
	private Boolean useResult;

	@Schema(description = "预览行数")
	private Integer pvdataNum;

	@Schema(description = "is enable")
	private Boolean enabled;

	@Schema(description = "version id")
	private Integer versionId;

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

	private String statement;


}
