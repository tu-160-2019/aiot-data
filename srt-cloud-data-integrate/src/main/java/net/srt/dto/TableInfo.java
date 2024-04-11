package net.srt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName TableInfo
 * @Author zrx
 * @Date 2023/10/13 9:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableInfo {
	private String tableName;
	private String tableCn;
	private List<ColumnInfo> columns;

}
