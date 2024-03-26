package net.srt.system.convert;

import net.srt.system.entity.SysDictTypeEntity;
import net.srt.system.vo.SysDictTypeVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SysDictTypeConvert {
    SysDictTypeConvert INSTANCE = Mappers.getMapper(SysDictTypeConvert.class);

    SysDictTypeVO convert(SysDictTypeEntity entity);

    SysDictTypeEntity convert(SysDictTypeVO vo);

    List<SysDictTypeVO> convertList(List<SysDictTypeEntity> list);

}
