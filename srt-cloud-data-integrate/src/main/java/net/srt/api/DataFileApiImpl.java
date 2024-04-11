package net.srt.api;

import lombok.RequiredArgsConstructor;
import net.srt.api.module.data.integrate.DataFileApi;
import net.srt.api.module.data.integrate.dto.DataFileDto;
import net.srt.convert.DataFileConvert;
import net.srt.framework.common.utils.Result;
import net.srt.service.DataFileService;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName DataAccessApiImpl
 * @Author zrx
 * @Date 2022/10/26 11:50
 */
@RestController
@RequiredArgsConstructor
public class DataFileApiImpl implements DataFileApi {

	private final DataFileService dataFileService;
	@Override
	public Result<DataFileDto> getById(Long id) {
		return Result.ok(DataFileConvert.INSTANCE.convertDto(dataFileService.getById(id)));
	}
}
