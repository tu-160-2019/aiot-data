package net.srt.convert;

import net.srt.api.module.data.integrate.dto.DataTableDto;
import net.srt.entity.DataTableEntity;
import net.srt.vo.DataTableVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 数据集成-贴源数据
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-11-07
 */
@Mapper
public interface DataOdsConvert {
	DataOdsConvert INSTANCE = Mappers.getMapper(DataOdsConvert.class);

	DataTableEntity convert(DataTableVO vo);

	DataTableEntity convertByDto(DataTableDto dto);

	DataTableVO convert(DataTableEntity entity);

	List<DataTableVO> convertList(List<DataTableEntity> list);

}
