package net.srt.api.module.data.integrate;

import net.srt.api.ServerNames;
import net.srt.api.module.data.integrate.dto.DataDatabaseDto;
import net.srt.framework.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @ClassName DataAccessApi
 * @Author zrx
 * @Date 2022/10/26 11:39
 */
@FeignClient(name = ServerNames.DATA_INTEGRATE_NAME, contextId = "data-integrate-database")
public interface DataDatabaseApi {
	/**
	 * 根据id获取
	 */
	@GetMapping(value = "api/data/integrate/database/{id}")
	Result<DataDatabaseDto> getById(@PathVariable Long id);
}
