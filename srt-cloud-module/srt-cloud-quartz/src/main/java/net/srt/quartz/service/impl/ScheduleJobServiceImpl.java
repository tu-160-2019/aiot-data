package net.srt.quartz.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import net.srt.framework.common.page.PageResult;
import net.srt.framework.mybatis.service.impl.BaseServiceImpl;
import net.srt.quartz.convert.ScheduleJobConvert;
import net.srt.quartz.dao.ScheduleJobDao;
import net.srt.quartz.entity.ScheduleJobEntity;
import net.srt.quartz.enums.ScheduleStatusEnum;
import net.srt.quartz.query.ScheduleJobQuery;
import net.srt.quartz.service.ScheduleJobService;
import net.srt.quartz.utils.ScheduleUtils;
import net.srt.quartz.vo.ScheduleJobVO;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * 定时任务
 *
 * @author 阿沐 babamu@126.com
 */
@Service
@RequiredArgsConstructor
public class ScheduleJobServiceImpl extends BaseServiceImpl<ScheduleJobDao, ScheduleJobEntity> implements ScheduleJobService {
	private final Scheduler scheduler;

	/**
	 * 启动项目时，初始化定时器
	 */
	@PostConstruct
	public void init() throws SchedulerException {
		scheduler.clear();
		List<ScheduleJobEntity> scheduleJobList = baseMapper.selectList(null);
		for (ScheduleJobEntity scheduleJob : scheduleJobList) {
			ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
		}
	}

	@Override
	public PageResult<ScheduleJobVO> page(ScheduleJobQuery query) {
		IPage<ScheduleJobEntity> page = baseMapper.selectPage(getPage(query), getWrapper(query));

		return new PageResult<>(ScheduleJobConvert.INSTANCE.convertList(page.getRecords()), page.getTotal());
	}

	private LambdaQueryWrapper<ScheduleJobEntity> getWrapper(ScheduleJobQuery query) {
		LambdaQueryWrapper<ScheduleJobEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.like(StrUtil.isNotBlank(query.getJobName()), ScheduleJobEntity::getJobName, query.getJobName());
		wrapper.like(StrUtil.isNotBlank(query.getJobGroup()), ScheduleJobEntity::getJobGroup, query.getJobGroup());
		wrapper.eq(query.getStatus() != null, ScheduleJobEntity::getStatus, query.getStatus())
				.orderByDesc(ScheduleJobEntity::getCreateTime).orderByDesc(ScheduleJobEntity::getId);
		dataScopeWithoutOrgId(wrapper);
		return wrapper;
	}

	@Override
	public void save(ScheduleJobVO vo) {
		ScheduleJobEntity entity = ScheduleJobConvert.INSTANCE.convert(vo);
		entity.setSaveLog(true);
		entity.setProjectId(getProjectId());
		entity.setOnce(false);
		entity.setStatus(ScheduleStatusEnum.PAUSE.getValue());
		if (baseMapper.insert(entity) > 0) {
			ScheduleUtils.createScheduleJob(scheduler, entity);
		}
	}

	@Override
	public void update(ScheduleJobVO vo) {
		ScheduleJobEntity entity = ScheduleJobConvert.INSTANCE.convert(vo);

		entity.setSaveLog(true);
		entity.setOnce(false);
		entity.setProjectId(getProjectId());
		// 更新定时任务
		if (updateById(entity)) {
			ScheduleJobEntity scheduleJob = getById(entity.getId());
			entity.setSaveLog(true);
			entity.setOnce(false);
			entity.setProjectId(getProjectId());
			ScheduleUtils.updateSchedulerJob(scheduler, scheduleJob);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(List<Long> idList) {
		for (Long id : idList) {
			ScheduleJobEntity scheduleJob = getById(id);

			// 删除定时任务
			if (removeById(id)) {
				ScheduleUtils.deleteScheduleJob(scheduler, scheduleJob);
			}
		}
	}

	@Override
	public void run(ScheduleJobVO vo) {
		ScheduleJobEntity scheduleJob = getById(vo.getId());
		if (scheduleJob == null) {
			return;
		}
		scheduleJob.setSaveLog(true);
		ScheduleUtils.run(scheduler, scheduleJob);
	}

	@Override
	public void changeStatus(ScheduleJobVO vo) {
		ScheduleJobEntity scheduleJob = getById(vo.getId());
		if (scheduleJob == null) {
			return;
		}
		scheduleJob.setSaveLog(true);
		// 更新数据
		scheduleJob.setStatus(vo.getStatus());
		updateById(scheduleJob);

		if (ScheduleStatusEnum.PAUSE.getValue().equals(vo.getStatus())) {
			ScheduleUtils.pauseJob(scheduler, scheduleJob);
		} else if (ScheduleStatusEnum.NORMAL.getValue().equals(vo.getStatus())) {
			ScheduleUtils.resumeJob(scheduler, scheduleJob);
		}
	}

	@Override
	public void pauseSystemJob(ScheduleJobEntity jobEntity) {
		//更新任务状态为暂停
		ScheduleJobEntity dbJob = getByTypeIdAndTypeAndOnce(jobEntity);
		if (dbJob != null) {
			dbJob.setStatus(ScheduleStatusEnum.PAUSE.getValue());
			updateById(dbJob);
		}
	}

	@Override
	public void buildSystemJob(ScheduleJobEntity jobEntity) {
		ScheduleJobEntity dbEntity = getByTypeIdAndTypeAndOnce(jobEntity);
		if (dbEntity == null) {
			save(jobEntity);
		} else {
			jobEntity.setId(dbEntity.getId());
			updateById(jobEntity);
		}
	}

	@Override
	public ScheduleJobEntity getByTypeIdAndTypeAndOnce(ScheduleJobEntity build) {
		LambdaQueryWrapper<ScheduleJobEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(ScheduleJobEntity::getTypeId, build.getTypeId()).eq(ScheduleJobEntity::getJobType, build.getJobType()).eq(ScheduleJobEntity::getOnce, build.getOnce()).last(" limit 1");
		return baseMapper.selectOne(wrapper);
	}

}
