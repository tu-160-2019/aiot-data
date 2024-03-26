package net.srt.quartz.task;

import cn.hutool.core.thread.ThreadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.srt.api.module.data.development.DataProductionScheduleApi;
import org.springframework.stereotype.Component;

/**
 * @ClassName DataProductionScheduleTask
 * @Author zrx
 * @Date 2023/1/19 14:39
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataProductionScheduleTask {
	private final DataProductionScheduleApi dataProductionScheduleApi;

	public void run(String id, Thread currentThread) {
		log.info("start run data-production-schedule");
		String recordId = dataProductionScheduleApi.scheduleRun(Long.parseLong(id)).getData();
		//查询调度状态
		do {
			ThreadUtil.sleep(5000);
			/*if (currentThread.isInterrupted()) {
				return;
			}*/
		} while (!dataProductionScheduleApi.scheduleComplete(Integer.parseInt(recordId)).getData());
		log.info("run data-production-schedule success");
	}

}
