package net.srt.api.module.data.service;

import net.srt.api.ServerNames;
import net.srt.api.module.data.service.dto.DataServiceApiAuthDto;
import net.srt.api.module.data.service.dto.DataServiceApiConfigDto;
import net.srt.framework.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @ClassName DataAccessApi
 * @Author zrx
 * @Date 2022/10/26 11:39
 */
@FeignClient(name = ServerNames.DATA_SERVICE_NAME, contextId = "data-service-api")
public interface DataServiceApi {
	/**
	 * 根据id获取
	 */
	@GetMapping(value = "api/api-config/{id}")
	Result<DataServiceApiConfigDto> getById(@PathVariable Long id);

	/**
	 * 添加授权
	 */
	@PostMapping(value = "api/api-auth")
	Result<String> auth(@RequestBody DataServiceApiAuthDto apiAuthDto);
}
