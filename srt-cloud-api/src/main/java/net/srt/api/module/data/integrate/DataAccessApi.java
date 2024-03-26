package net.srt.api.module.data.integrate;

import net.srt.api.ServerNames;
import net.srt.api.module.data.integrate.dto.DataAccessDto;
import net.srt.api.module.data.integrate.dto.DataAccessIncreaseLogDto;
import net.srt.api.module.data.integrate.dto.DataAccessTaskDto;
import net.srt.api.module.data.integrate.dto.PreviewNameMapperDto;
import net.srt.api.module.data.integrate.dto.QueryIncreaseLog;
import net.srt.framework.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import srt.cloud.framework.dbswitch.data.domain.DbSwitchResult;
import srt.cloud.framework.dbswitch.data.domain.DbSwitchTableResult;

import java.util.List;

/**
 * @ClassName DataAccessApi
 * @Author zrx
 * @Date 2022/10/26 11:39
 */
@FeignClient(name = ServerNames.DATA_INTEGRATE_NAME, contextId = "data-integrate-access")
public interface DataAccessApi {
	/**
	 * 根据id获取
	 */
	@GetMapping(value = "api/data/integrate/access/{id}")
	Result<DataAccessDto> getById(@PathVariable Long id);

	@PostMapping(value = "api/data/integrate/access/task")
	Result<Long> addTask(@RequestBody DataAccessTaskDto dataAccessTaskDto);

	@PutMapping(value = "api/data/integrate/access/task")
	void updateTask(@RequestBody DataAccessTaskDto dataAccessTaskDto);

	@PostMapping(value = "api/data/integrate/access/task-detail/{projectId}/{orgId}/{creator}/{taskId}/{dataAccessId}")
	void addTaskDetail(@PathVariable Long projectId, @PathVariable Long orgId, @PathVariable Long creator, @PathVariable Long taskId, @PathVariable Long dataAccessId, @RequestBody DbSwitchTableResult tableResult);

	/**
	 * 根据任务id获取任务
	 */
	@GetMapping(value = "api/quartz/access-task/{id}")
	Result<DataAccessTaskDto> getTaskById(@PathVariable Long id);

	@GetMapping(value = "api/quartz/access-task/table-map/{id}")
	Result<List<PreviewNameMapperDto>> getTableMap(@PathVariable Long id);

	@GetMapping(value = "api/quartz/access-task/column-map/{id}")
	Result<List<PreviewNameMapperDto>> getColumnMap(@PathVariable Long id, @RequestParam String tableName);

	@PostMapping(value = "api/quartz/access-increase-log/query")
	DataAccessIncreaseLogDto getNewestLog(@RequestBody QueryIncreaseLog queryIncreaseLog);

	@PostMapping(value = "api/quartz/access-increase-log/add")
	void addIncreaseLog(@RequestBody DataAccessIncreaseLogDto increaseLogDto);
}
