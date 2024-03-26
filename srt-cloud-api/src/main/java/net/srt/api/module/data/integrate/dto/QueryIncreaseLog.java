package net.srt.api.module.data.integrate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName QueryIncreaseLog
 * @Author zrx
 * @Date 2024/2/26 15:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryIncreaseLog {
	private Long dataAccessId;
	private Long sourceDatabaseId;
	private Long targetDatabaseId;
	private String schemaName;
	private String tableName;
	private String increaseColumn;
}
