package net.srt.api.module.quartz;

import net.srt.api.ServerNames;
import net.srt.framework.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 数据集成-数据接入 定时api
 *
 * @author 阿沐 babamu@126.com
 */
@FeignClient(name = ServerNames.QUARTZ_SERVER_NAME, contextId = "quartz-data-development-production-schedule")
public interface QuartzDataProductionScheduleApi {

	/**
	 * 发布作业调度任务
	 */
	@PostMapping(value = "api/quartz/development-schedule/release/{id}")
	Result<String> release(@PathVariable Long id);

	/**
	 * 取消作业调度任务
	 */
	@PostMapping(value = "api/quartz/development-schedule/cancle/{id}")
	Result<String> cancle(@PathVariable Long id);

}
