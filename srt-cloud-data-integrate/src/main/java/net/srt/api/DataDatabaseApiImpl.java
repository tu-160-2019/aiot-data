package net.srt.api;

import lombok.RequiredArgsConstructor;
import net.srt.api.module.data.integrate.DataDatabaseApi;
import net.srt.api.module.data.integrate.dto.DataDatabaseDto;
import net.srt.convert.DataDatabaseConvert;
import net.srt.framework.common.utils.Result;
import net.srt.service.DataDatabaseService;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName DataDatabaseApiImpl
 * @Author zrx
 * @Date 2022/10/26 11:50
 */
@RestController
@RequiredArgsConstructor
public class DataDatabaseApiImpl implements DataDatabaseApi {

	private final DataDatabaseService databaseService;

	@Override
	public Result<DataDatabaseDto> getById(Long id) {
		return Result.ok(DataDatabaseConvert.INSTANCE.convertDto(databaseService.getById(id)));
	}
}
