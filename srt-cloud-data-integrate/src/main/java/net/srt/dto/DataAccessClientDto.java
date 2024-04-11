package net.srt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import srt.cloud.framework.dbswitch.common.constant.ChangeDataSyncType;
import srt.cloud.framework.dbswitch.common.entity.MapperConfig;
import srt.cloud.framework.dbswitch.common.entity.PatternMapper;

import java.util.List;
import java.util.Map;

/**
 * @ClassName DataAccessDto
 * @Author zrx
 * @Date 2022/10/25 16:26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "数据接入任务前端dto")
public class DataAccessClientDto {
	@Schema(description = "主键id")
	private Long id;
	@Schema(description = "任务名称")
	private String taskName;
	@Schema(description = "描述")
	private String description;
	@Schema(description = "项目id")
	private Long projectId;
	@Schema(description = "源端数据库id")
	private Long sourceDatabaseId;
	@Schema(description = "目的端数据库id")
	private Long targetDatabaseId;
	@Schema(description = "接入方式 1-ods接入 2-自定义接入")
	private Integer accessMode;
	@Schema(description = "任务类型")
	private Integer taskType;
	@Schema(description = "cron表达式")
	private String cron;
	@Schema(description = "包含表或排除表")
	private Integer includeOrExclude;
	@Schema(description = "源端选择的表")
	private List<String> sourceSelectedTables;
	@Schema(description = "只创建表")
	private boolean targetOnlyCreate;
	@Schema(description = "同步已存在的表")
	private boolean targetSyncExit;
	@Schema(description = "同步前是否删除表")
	private boolean targetDropTable;
	@Schema(description = "开启增量变更同步")
	private boolean targetDataSync;
	@Schema(description = "同步索引")
	private boolean targetIndexCreate;
	@Schema(description = "表名字段名转小写")
	private boolean targetLowerCase;
	@Schema(description = "表名字段名转大写")
	private boolean targetUpperCase;
	@Schema(description = "主键递增")
	private boolean targetAutoIncrement;
	@Schema(description = "批处理量")
	private Integer batchSize;
	@Schema(description = "表名映射")
	private List<PatternMapper> tableNameMapper;
	@Schema(description = "字段名名映射")
	private List<PatternMapper> columnNameMapper;
	private Long orgId;
	@Schema(description = "源端类型 1-数据库 2-sql")
	private Integer sourceType;
	@Schema(description = "自定义采集语句（sql时有此值）")
	private String sourceSql;
	@Schema(description = "目标表（sql时有此值）")
	private String targetTable;
	@Schema(description = "映射类型")
	private String mapperType;
	@Schema(description = "唯一键列表（sql时有此值）")
	private List<String> sourcePrimaryKeys;
	@Schema(description = "增量字段")
	private String increaseColumnName;
	@Schema(description = "单表映射配置")
	private Map<String, MapperConfig> configMap;
	@Schema(description = "增量变更类型")
	private String changeDataSyncType;
}
