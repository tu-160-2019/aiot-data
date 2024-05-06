package com.ai.tts.convert;

import com.ai.tts.entity.DataTtsEntity;
import com.ai.tts.vo.DataTtsVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DataTtsConvert {
    DataTtsConvert INSTANCE = Mappers.getMapper(DataTtsConvert.class);

    DataTtsEntity convert(DataTtsVo vo);

    DataTtsVo convert(DataTtsEntity entity);

    List<DataTtsVo> convertList(List<DataTtsEntity> list);
}
