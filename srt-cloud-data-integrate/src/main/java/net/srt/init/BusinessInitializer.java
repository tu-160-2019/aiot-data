package net.srt.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.srt.entity.DataProjectEntity;
import net.srt.service.DataAccessTaskService;
import net.srt.service.DataProjectService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName BusinessInitializer
 * @Author zrx
 * @Date 2022/11/27 12:14
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class BusinessInitializer implements ApplicationRunner {

	private final DataProjectService dataProjectService;
	private final DataAccessTaskService accessTaskService;

	@Override
	public void run(ApplicationArguments args) {
		initProject();
		initScheduleMonitor();
	}

	private void initProject() {
		log.info("init project cache");
		List<DataProjectEntity> projectEntities = dataProjectService.list();
		//把所有项目放入本地缓存
		for (DataProjectEntity project : projectEntities) {
			dataProjectService.initDb(project);
		}
		log.info("init project cache end");
	}

	/**
	 * init task monitor
	 */
	private void initScheduleMonitor() {
		//处理没执行完的同步任务
		accessTaskService.dealNotFinished();
	}

}
