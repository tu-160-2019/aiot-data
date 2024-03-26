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
@FeignClient(name = ServerNames.DATA_GOVERNANCE_NAME, contextId = "data-governance-metadata-collect")
public interface DataMetadataCollectApi {
	/**
	 * 根据id获取采集任务
	 */
	@GetMapping(value = "api/data/governance/metadata-collect/{id}")
	Result<DataGovernanceMetadataCollectDto> getById(@PathVariable Long id);

	/**
	 * 根据id获取采集任务
	 */
	@PostMapping(value = "api/data/governance/metadata-collect-record")
	DataGovernanceMetadataCollectRecordDto addCollectRecord(@RequestBody DataGovernanceMetadataCollectRecordDto collectRecordDto);

	/**
	 * 根据id获取采集任务
	 */
	@PutMapping(value = "api/data/governance/metadata-collect-record")
	void upCollectRecord(@RequestBody DataGovernanceMetadataCollectRecordDto collectRecordDto);

	/**
	 * 根据父级id和数据源id获取
	 */
	@GetMapping(value = "api/data/governance/metadata/datasource")
	Result<DataGovernanceMetadataDto> getByParentIdAndDatasourceId(@RequestParam Long parnetId, @RequestParam Long datasourceId);

	/**
	 * 根据父级id和数据源id获取
	 */
	@GetMapping(value = "api/data/governance/metadata/info")
	Result<DataGovernanceMetadataDto> getMetadataById(@RequestParam Long metadataId);

	/**
	 * 根据父级id和code以及modelId获取
	 */
	@GetMapping(value = "api/data/governance/metadata/child-info")
	Result<DataGovernanceMetadataDto> getByParentIdAndOtherInfo(@RequestParam Long parnetId, @RequestParam Long datasourceId, @RequestParam String code, @RequestParam Long metamodelId);

	/**
	 * 添加元数据
	 */
	@PostMapping(value = "api/data/governance/metadata")
	DataGovernanceMetadataDto addOrUpdateMetadata(@RequestBody DataGovernanceMetadataDto metadataDto);


	/**
	 * 获取元数据属性
	 */
	@GetMapping(value = "api/data/governance/metadata-property")
	Result<DataGovernanceMetadataPropertyDto> getByPropertyIdAndMetadataId(@RequestParam Long propertyId, @RequestParam Long metadataId);

	/**
	 * 添加元数据属性
	 */
	@PostMapping(value = "api/data/governance/metadata-prpperty")
	void addOrUpdateMetadataProperty(@RequestBody DataGovernanceMetadataPropertyDto metadataPropertyDto);

	@GetMapping(value = "api/data/governance/metadata/list")
	Result<List<DataGovernanceMetadataDto>> listParentIdAndDatasourceId(@RequestParam Long parentId, @RequestParam Long datasourceId, @RequestParam Long metamodelId);

	@DeleteMapping(value = "api/data/governance/metadata")
	void deleteMetadata(@RequestParam Long id);

	@GetMapping(value = "api/data/governance/by-datasourceId")
	Result<DataGovernanceMetadataCollectDto> getByDatasourceId(Long id);

	@GetMapping(value = "api/data/governance/metadata/by-datasourceId")
	Result<DataGovernanceMetadataDto> getMetadataByDatasourceId(Long id);

}
