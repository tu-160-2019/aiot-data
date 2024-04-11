package net.srt.convert;

import net.srt.entity.DataLayerEntity;
import net.srt.vo.DataLayerVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
* 数仓分层
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-10-08
*/
@Mapper
public interface DataLayerConvert {
    DataLayerConvert INSTANCE = Mappers.getMapper(DataLayerConvert.class);

    DataLayerEntity convert(DataLayerVO vo);

    DataLayerVO convert(DataLayerEntity entity);

    List<DataLayerVO> convertList(List<DataLayerEntity> list);

}