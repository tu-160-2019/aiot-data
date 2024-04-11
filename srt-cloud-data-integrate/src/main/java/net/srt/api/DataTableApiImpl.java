package net.srt.api;

import lombok.RequiredArgsConstructor;
import net.srt.api.module.data.integrate.DataTableApi;
import net.srt.api.module.data.integrate.dto.DataTableDto;
import net.srt.convert.DataOdsConvert;
import net.srt.entity.DataTableEntity;
import net.srt.framework.common.utils.Result;
import net.srt.service.DataTableService;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName DataAccessApiImpl
 * @Author zrx
 * @Date 2022/10/26 11:50
 */
@RestController
@RequiredArgsConstructor
public class DataTableApiImpl implements DataTableApi {

	private final DataTableService dataTableService;

	@Override
	public Result<String> addOds(DataTableDto dataTableDto) {
		DataTableEntity dataTableEntity = dataTableService.getByTableName(dataTableDto.getProjectId(), dataTableDto.getTableName());
		if (dataTableEntity == null) {
			dataTableService.save(DataOdsConvert.INSTANCE.convertByDto(dataTableDto));
		} else {
			dataTableDto.setId(dataTableEntity.getId());
			dataTableService.updateById(DataOdsConvert.INSTANCE.convertByDto(dataTableDto));
		}
		return Result.ok();
	}
}
