package srt.cloud.framework.dbswitch.data.domain;

import lombok.Data;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @ClassName DbSwitchTable
 * @Author zrx
 * @Date 2022/10/28 9:37
 */
@Data
public class DbSwitchTableResult {
	private String sourceSchemaName;
	private String sourceTableName;
	private String targetSchemaName;
	private String targetTableName;
	private String tableRemarks;
	private Date syncTime;
	private AtomicLong syncCount = new AtomicLong(0);
	private AtomicLong syncBytes = new AtomicLong(0);
	private AtomicBoolean ifSuccess = new AtomicBoolean(true);
	private String errorMsg;
	private String successMsg = "同步成功";
}
