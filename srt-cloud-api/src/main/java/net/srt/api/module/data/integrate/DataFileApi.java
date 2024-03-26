package net.srt.api.module.data.integrate;

import net.srt.api.ServerNames;
import net.srt.api.module.data.integrate.dto.DataFileDto;
import net.srt.framework.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @ClassName DataAccessApi
 * @Author zrx
 * @Date 2022/10/26 11:39
 */
@FeignClient(name = ServerNames.DATA_INTEGRATE_NAME, contextId = "data-integrate-file")
public interface DataFileApi {
	/**
	 * 根据id获取
	 */
	@PostMapping(value = "api/file/{id}")
	Result<DataFileDto> getById(@PathVariable Long id);
}
