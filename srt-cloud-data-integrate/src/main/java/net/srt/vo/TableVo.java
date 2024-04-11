package net.srt.vo;

import lombok.Data;
import srt.cloud.framework.dbswitch.common.type.DBTableType;

/**
 * @ClassName TableVo
 * @Author zrx
 * @Date 2022/10/24 9:14
 */
@Data
public class TableVo {
	private String tableName;
	private String remarks;
	private DBTableType tableType;
}
