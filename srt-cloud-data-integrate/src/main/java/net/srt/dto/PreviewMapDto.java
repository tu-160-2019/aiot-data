package net.srt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import srt.cloud.framework.dbswitch.common.entity.PatternMapper;

import java.util.List;

/**
 * @ClassName PreviewMapDto
 * @Author zrx
 * @Date 2022/10/27 9:46
 */
@Data
public class PreviewMapDto {

	private Integer sourceType;
	private Long sourceDatabaseId;
	private String sourceSql;
	private Integer includeOrExclude;

	private List<String> sourceSelectedTables;
	@Schema(description = "表名映射")
	private List<PatternMapper> tableNameMapper;

	private String preiveTableName;
	@Schema(description = "字段名映射")
	private List<PatternMapper> columnNameMapper;

	private boolean targetLowerCase;
	private boolean targetUpperCase;
	private String tablePrefix;
}
