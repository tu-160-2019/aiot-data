package net.srt.api.module.data.governance;

import net.srt.api.ServerNames;
import net.srt.api.module.data.governance.dto.DataGovernanceQualityConfigDto;
import net.srt.api.module.data.governance.dto.DataGovernanceQualityTaskColumnDto;
import net.srt.api.module.data.governance.dto.DataGovernanceQualityTaskDto;
import net.srt.api.module.data.governance.dto.DataGovernanceQualityTaskTableDto;
import net.srt.framework.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @ClassName DataAccessApi
 * @Author zrx
 * @Date 2022/10/26 11:39
 */
@FeignClient(name = ServerNames.DATA_GOVERNANCE_NAME, contextId = "data-governance-quality")
public interface DataQualityApi {

	@GetMapping(value = "api/data/governance/quality-config/{id}")
	Result<DataGovernanceQualityConfigDto> getById(@PathVariable Long id);

	@PostMapping(value = "api/data/governance/add-quality-task")
	Result<DataGovernanceQualityTaskDto> addQualityTask(@RequestBody DataGovernanceQualityTaskDto qualityTaskDto);

	@PutMapping(value = "api/data/governance/update-quality-task")
	Result<String> updateQualityTask(@RequestBody DataGovernanceQualityTaskDto qualityTaskDto);

	@PostMapping(value = "api/data/governance/add-quality-task-table")
	Result<DataGovernanceQualityTaskTableDto> addTaskTable(@RequestBody DataGovernanceQualityTaskTableDto qualityTaskTableDto);

	@PutMapping(value = "api/data/governance/update-quality-task-table")
	Result<String> updateQualityTaskTable(@RequestBody DataGovernanceQualityTaskTableDto taskTable);

	@PostMapping(value = "api/data/governance/add-quality-task-column")
	Result<String> addQualityTaskColumns(@RequestBody List<DataGovernanceQualityTaskColumnDto> columnDtos);
}
