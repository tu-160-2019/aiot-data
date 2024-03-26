package net.srt.api.module.data.governance;

import net.srt.api.ServerNames;
import net.srt.api.module.data.governance.dto.DataGovernanceMasterDistributeDto;
import net.srt.api.module.data.governance.dto.DataGovernanceMasterDistributeLogDto;
import net.srt.api.module.data.governance.dto.DataGovernanceMasterModelDto;
import net.srt.framework.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @ClassName DataAccessApi
 * @Author zrx
 * @Date 2022/10/26 11:39
 */
@FeignClient(name = ServerNames.DATA_GOVERNANCE_NAME, contextId = "data-governance-master")
public interface DataMasterApi {

	@GetMapping(value = "api/data/governance/master-distribute/{id}")
	Result<DataGovernanceMasterDistributeDto> getDistributeById(@PathVariable Long id);

	@PostMapping(value = "api/data/governance/add-master-distribute-log")
	Result<DataGovernanceMasterDistributeLogDto> addDistributeLog(@RequestBody DataGovernanceMasterDistributeLogDto distributeLogDto);

	@PostMapping(value = "api/data/governance/up-master-distribute-log")
	void upDistributeLog(@RequestBody DataGovernanceMasterDistributeLogDto distributeLogDto);

	@GetMapping(value = "api/data/governance/master-model/{masterModelId}")
	Result<DataGovernanceMasterModelDto> getMasterModelById(@PathVariable Long masterModelId);

	//测试派发用
	@PostMapping(value = "api/test-distribute")
	Result<String> testDistribute(@RequestBody List<Map<String, Object>> listBody);
}
