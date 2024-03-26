package net.srt.api.module.data.governance;

import net.srt.api.ServerNames;
import net.srt.api.module.data.governance.dto.DataGovernanceMetadataCollectDto;
import net.srt.api.module.data.governance.dto.DataGovernanceMetadataCollectRecordDto;
import net.srt.api.module.data.governance.dto.DataGovernanceMetadataDto;
import net.srt.api.module.data.governance.dto.DataGovernanceMetadataPropertyDto;
import net.srt.framework.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @ClassName DataAccessApi
 * @Author zrx
 * @Date 2022/10/26 11:39
 */
@FeignClient(name = ServerNames.DATA_GOVERNANCE_NAME, contextId = "data-governance-metadata")
public interface DataMetadataApi {
	/**
	 * 根据id获取采集任务
	 */
	@GetMapping(value = "api/data/governance/metadata/{id}")
	Result<DataGovernanceMetadataDto> getById(@PathVariable Integer id);

}
