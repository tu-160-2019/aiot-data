package net.srt.quartz.service;

import net.srt.framework.common.page.PageResult;
import net.srt.framework.mybatis.service.BaseService;
import net.srt.quartz.entity.ScheduleJobEntity;
import net.srt.quartz.query.ScheduleJobQuery;
import net.srt.quartz.vo.ScheduleJobVO;

import java.util.List;

/**
 * 定时任务
 *
 * @author 阿沐 babamu@126.com
 */
public interface ScheduleJobService extends BaseService<ScheduleJobEntity> {

    PageResult<ScheduleJobVO> page(ScheduleJobQuery query);

    void save(ScheduleJobVO vo);

    void update(ScheduleJobVO vo);

    void delete(List<Long> idList);

    void run(ScheduleJobVO vo);

    void changeStatus(ScheduleJobVO vo);

	void pauseSystemJob(ScheduleJobEntity jobEntity);

	void buildSystemJob(ScheduleJobEntity jobEntity);

	ScheduleJobEntity getByTypeIdAndTypeAndOnce(ScheduleJobEntity build);
}
