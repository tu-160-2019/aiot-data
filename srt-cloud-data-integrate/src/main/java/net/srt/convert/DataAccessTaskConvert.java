package net.srt.convert;

import net.srt.api.module.data.integrate.dto.DataAccessTaskDto;
import net.srt.entity.DataAccessTaskEntity;
import net.srt.vo.DataAccessTaskVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
* 数据接入任务记录
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-10-24
*/
@Mapper
public interface DataAccessTaskConvert {
    DataAccessTaskConvert INSTANCE = Mappers.getMapper(DataAccessTaskConvert.class);

    DataAccessTaskEntity convert(DataAccessTaskVO vo);

	DataAccessTaskEntity convertByDto(DataAccessTaskDto dto);

    DataAccessTaskVO convert(DataAccessTaskEntity entity);

	DataAccessTaskDto convertDto(DataAccessTaskEntity entity);

    List<DataAccessTaskVO> convertList(List<DataAccessTaskEntity> list);

}
