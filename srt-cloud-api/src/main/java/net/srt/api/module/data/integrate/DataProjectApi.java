package net.srt.api.module.data.integrate;

import net.srt.api.ServerNames;
import net.srt.framework.common.cache.bean.DataProjectCacheBean;
import net.srt.framework.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @ClassName DataAccessApi
 * @Author zrx
 * @Date 2022/10/26 11:39
 */
@FeignClient(name = ServerNames.DATA_INTEGRATE_NAME, contextId = "data-integrate-project")
public interface DataProjectApi {
	/**
	 * 根据id获取
	 */
	@GetMapping(value = "api/data/integrate/project/list-all")
	Result<List<DataProjectCacheBean>> getProjectList();

	/**
	 * 根据id获取
	 */
	@GetMapping(value = "api/data/integrate/project/{id}")
	Result<DataProjectCacheBean> getById(@PathVariable Long id);

	/**
	 * 根据id获取
	 */
	@PostMapping(value = "api/data/integrate/project/{id}/{userId}")
	Result<String> addProjectRel(@PathVariable Long id, @PathVariable Long userId);

}
