package net.srt.api.module.data.development;

import net.srt.api.ServerNames;
import net.srt.api.module.data.development.dto.DataProductionTaskDto;
import net.srt.api.module.data.integrate.dto.DataAccessDto;
import net.srt.api.module.data.integrate.dto.DataAccessTaskDto;
import net.srt.framework.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import srt.cloud.framework.dbswitch.data.domain.DbSwitchTableResult;

/**
 * @ClassName DataProductionTaskApi
 * @Author zrx
 * @Date 2022/10/26 11:39
 */
@FeignClient(name = ServerNames.DATA_DEVELOPMENT_NAME, contextId = "data-development-production-task")
public interface DataProductionTaskApi {
	/**
	 * 根据databaseId获取
	 */
	@GetMapping(value = "api/data/development/production-task/{databaseId}")
	Result<DataProductionTaskDto> getByDbId(@PathVariable Long databaseId);
}
