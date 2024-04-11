package net.srt.convert;

import net.srt.api.module.data.integrate.dto.DataAccessDto;
import net.srt.entity.DataAccessEntity;
import net.srt.vo.DataAccessVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 数据集成-数据接入
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-10-24
 */
@Mapper
public interface DataAccessConvert {
	DataAccessConvert INSTANCE = Mappers.getMapper(DataAccessConvert.class);

	DataAccessEntity convert(DataAccessVO vo);

	DataAccessVO convert(DataAccessEntity entity);

	DataAccessDto convertDto(DataAccessEntity entity);

	List<DataAccessVO> convertList(List<DataAccessEntity> list);

}
