package net.srt.convert;

import net.srt.entity.DataProjectEntity;
import net.srt.vo.DataProjectVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
* 数据项目
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-09-27
*/
@Mapper
public interface DataProjectConvert {
    DataProjectConvert INSTANCE = Mappers.getMapper(DataProjectConvert.class);

    DataProjectEntity convert(DataProjectVO vo);

    DataProjectVO convert(DataProjectEntity entity);

    List<DataProjectVO> convertList(List<DataProjectEntity> list);

}