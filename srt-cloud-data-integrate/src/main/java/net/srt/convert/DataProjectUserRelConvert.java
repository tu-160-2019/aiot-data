package net.srt.convert;

import net.srt.entity.DataProjectUserRelEntity;
import net.srt.vo.DataProjectUserRelVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
* 项目用户关联表
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-10-08
*/
@Mapper
public interface DataProjectUserRelConvert {
    DataProjectUserRelConvert INSTANCE = Mappers.getMapper(DataProjectUserRelConvert.class);

    DataProjectUserRelEntity convert(DataProjectUserRelVO vo);

    DataProjectUserRelVO convert(DataProjectUserRelEntity entity);

    List<DataProjectUserRelVO> convertList(List<DataProjectUserRelEntity> list);

}