package net.srt.convert;

import net.srt.api.module.data.integrate.dto.DataDatabaseDto;
import net.srt.entity.DataDatabaseEntity;
import net.srt.vo.DataDatabaseVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
* 数据集成-数据库管理
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-10-09
*/
@Mapper
public interface DataDatabaseConvert {
    DataDatabaseConvert INSTANCE = Mappers.getMapper(DataDatabaseConvert.class);

    DataDatabaseEntity convert(DataDatabaseVO vo);

    DataDatabaseVO convert(DataDatabaseEntity entity);

	DataDatabaseDto convertDto(DataDatabaseEntity entity);

    List<DataDatabaseVO> convertList(List<DataDatabaseEntity> list);

}
