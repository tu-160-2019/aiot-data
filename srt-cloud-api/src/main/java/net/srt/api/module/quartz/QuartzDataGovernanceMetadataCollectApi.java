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
@FeignClient(name = ServerNames.QUARTZ_SERVER_NAME, contextId = "quartz-data-metadata-collect")
public interface QuartzDataGovernanceMetadataCollectApi {

	/**
	 * 发布数据接入任务
	 */
	@PostMapping(value = "api/quartz/metadata-collect/release/{id}")
	Result<String> release(@PathVariable Long id);

	/**
	 * 取消数据接入任务
	 */
	@PostMapping(value = "api/quartz/metadata-collect/cancel/{id}")
	Result<String> cancel(@PathVariable Long id);

	/**
	 * 手动执行
	 */
	@PostMapping(value = "api/quartz/metadata-collect/hand-run/{id}")
	Result<String> handRun(@PathVariable Long id);
}
