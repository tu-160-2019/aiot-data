package net.srt.api.module.quartz;

import net.srt.api.ServerNames;
import net.srt.framework.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 主数据派发api
 *
 * @author zrx
 */
@FeignClient(name = ServerNames.QUARTZ_SERVER_NAME, contextId = "quartz-master-distribute")
public interface QuartzDataGovernanceMasterApi {

	@PostMapping(value = "api/quartz/master-distribute/release/{id}")
	Result<String> release(@PathVariable Long id);

	/**
	 * 取消数据接入任务
	 */
	@PostMapping(value = "api/quartz/master-distribute/cancel/{id}")
	Result<String> cancel(@PathVariable Long id);

	/**
	 * 手动执行
	 */
	@PostMapping(value = "api/quartz/master-distribute/hand-run/{id}")
	Result<String> handRun(@PathVariable Long id);
}
