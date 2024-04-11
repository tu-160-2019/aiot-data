package net.srt.convert;

import net.srt.api.module.data.integrate.dto.DataAccessIncreaseLogDto;
import net.srt.entity.DataAccessIncreaseLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 数据集成-数据增量接入日志
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2024-02-26
 */
@Mapper
public interface DataAccessIncreaseLogConvert {
	DataAccessIncreaseLogConvert INSTANCE = Mappers.getMapper(DataAccessIncreaseLogConvert.class);

	DataAccessIncreaseLogDto convert(DataAccessIncreaseLogEntity entity);

	DataAccessIncreaseLogEntity convert(DataAccessIncreaseLogDto dto);
}
