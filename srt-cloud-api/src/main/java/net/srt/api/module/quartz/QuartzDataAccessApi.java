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
@FeignClient(name = ServerNames.QUARTZ_SERVER_NAME, contextId = "quartz-data-integrate-access")
public interface QuartzDataAccessApi {

	/**
	 * 发布数据接入任务
	 */
	@PostMapping(value = "api/quartz/access/release/{id}")
	Result<String> releaseAccess(@PathVariable Long id);

	/**
	 * 取消数据接入任务
	 */
	@PostMapping(value = "api/quartz/access/cancle/{id}")
	Result<String> cancleAccess(@PathVariable Long id);

	/**
	 * 手动执行
	 */
	@PostMapping(value = "api/quartz/access/hand-run/{id}")
	Result<String> handRun(@PathVariable Long id);

	/**
	 * 终止手动执行的任务
	 */
	@PostMapping(value = "api/quartz/access/stop-task/{executeNo}")
	Result<String> stopHandTask(@PathVariable String executeNo);

}
