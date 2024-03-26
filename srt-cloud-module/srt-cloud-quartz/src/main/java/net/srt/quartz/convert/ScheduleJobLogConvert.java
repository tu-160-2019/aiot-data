package net.srt.quartz.convert;

import net.srt.quartz.entity.ScheduleJobLogEntity;
import net.srt.quartz.vo.ScheduleJobLogVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
* 定时任务日志
*
* @author 阿沐 babamu@126.com
*/
@Mapper
public interface ScheduleJobLogConvert {
    ScheduleJobLogConvert INSTANCE = Mappers.getMapper(ScheduleJobLogConvert.class);

    ScheduleJobLogEntity convert(ScheduleJobLogVO vo);

    ScheduleJobLogVO convert(ScheduleJobLogEntity entity);

    List<ScheduleJobLogVO> convertList(List<ScheduleJobLogEntity> list);

}
