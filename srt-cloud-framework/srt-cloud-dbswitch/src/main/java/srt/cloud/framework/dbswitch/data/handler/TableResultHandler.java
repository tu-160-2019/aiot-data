package srt.cloud.framework.dbswitch.data.handler;

import srt.cloud.framework.dbswitch.data.domain.DbSwitchTableResult;

/**
 * @ClassName TableResultHandler
 * @Author zrx
 * @Date 2022/11/24 20:47
 */
public interface TableResultHandler {
	void handler(DbSwitchTableResult tableResult);
}
