package srt.cloud.framework.dbswitch.data.domain;

import lombok.Data;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @ClassName DbSwitchResult
 * @Author zrx
 * @Date 2022/10/28 9:36
 */
@Data
public class DbSwitchResult {
	private AtomicBoolean ifAllSuccess = new AtomicBoolean(true);
	private AtomicLong totalTableCount = new AtomicLong(0);
	private AtomicLong totalTableSuccessCount = new AtomicLong(0);
	private AtomicLong totalTableFailCount = new AtomicLong(0);
	private AtomicLong totalRowCount = new AtomicLong(0);
	private AtomicLong totalBytes = new AtomicLong(0);
	private List<DbSwitchTableResult> tableResultList = new CopyOnWriteArrayList<>();
}
