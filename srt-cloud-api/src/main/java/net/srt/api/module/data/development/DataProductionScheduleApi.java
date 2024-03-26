package net.srt.api.module.data.development;

import net.srt.api.ServerNames;
import net.srt.api.module.data.development.dto.DataProductionScheduleDto;
import net.srt.api.module.data.development.dto.DataProductionTaskDto;
import net.srt.framework.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @ClassName DataProductionTaskApi
 * @Author zrx
 * @Date 2022/10/26 11:39
 */
@FeignClient(name = ServerNames.DATA_DEVELOPMENT_NAME, contextId = "data-development-production-schedule")
public interface DataProductionScheduleApi {
	/**
	 * 根据id获取作业调度任务信息
	 */
	@GetMapping(value = "api/data/development/production-schedule/{id}")
	Result<DataProductionScheduleDto> getById(@PathVariable Long id);

	/**
	 * 根据id执行作业调度任务
	 */
	@GetMapping(value = "api/data/development/production-schedule/run/{id}")
	Result<String> scheduleRun(@PathVariable Long id);

	/**
	 * 根据调度记录id查询作业是否执行完毕
	 */
	@GetMapping(value = "api/data/development/production-schedule/complete/{recordId}")
	Result<Boolean> scheduleComplete(@PathVariable Integer recordId);
}
