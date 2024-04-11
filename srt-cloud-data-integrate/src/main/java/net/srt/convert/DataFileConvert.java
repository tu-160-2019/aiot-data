package net.srt.convert;

import net.srt.api.module.data.integrate.dto.DataFileDto;
import net.srt.entity.DataFileEntity;
import net.srt.vo.DataFileVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
* 文件表
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-11-16
*/
@Mapper
public interface DataFileConvert {
    DataFileConvert INSTANCE = Mappers.getMapper(DataFileConvert.class);

    DataFileEntity convert(DataFileVO vo);

    DataFileVO convert(DataFileEntity entity);

	DataFileDto convertDto(DataFileEntity entity);

    List<DataFileVO> convertList(List<DataFileEntity> list);

}
