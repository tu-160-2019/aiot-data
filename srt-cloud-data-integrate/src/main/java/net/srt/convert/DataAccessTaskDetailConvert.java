package net.srt.convert;

import net.srt.entity.DataAccessTaskDetailEntity;
import net.srt.vo.DataAccessTaskDetailVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
* 数据接入-同步记录详情
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-10-28
*/
@Mapper
public interface DataAccessTaskDetailConvert {
    DataAccessTaskDetailConvert INSTANCE = Mappers.getMapper(DataAccessTaskDetailConvert.class);

    DataAccessTaskDetailEntity convert(DataAccessTaskDetailVO vo);

    DataAccessTaskDetailVO convert(DataAccessTaskDetailEntity entity);

    List<DataAccessTaskDetailVO> convertList(List<DataAccessTaskDetailEntity> list);

}