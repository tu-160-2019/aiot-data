package net.srt.convert;

import net.srt.entity.DataFileCategoryEntity;
import net.srt.vo.DataFileCategoryVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
* 文件分组表
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-11-12
*/
@Mapper
public interface DataFileCategoryConvert {
    DataFileCategoryConvert INSTANCE = Mappers.getMapper(DataFileCategoryConvert.class);

    DataFileCategoryEntity convert(DataFileCategoryVO vo);

    DataFileCategoryVO convert(DataFileCategoryEntity entity);

    List<DataFileCategoryVO> convertList(List<DataFileCategoryEntity> list);

}