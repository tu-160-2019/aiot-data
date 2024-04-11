package net.srt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import net.srt.constants.CommonRunStatus;
import net.srt.convert.DataAccessTaskConvert;
import net.srt.dao.DataAccessTaskDao;
import net.srt.entity.DataAccessTaskEntity;
import net.srt.framework.common.page.PageResult;
import net.srt.framework.common.utils.DateUtils;
import net.srt.framework.mybatis.service.impl.BaseServiceImpl;
import net.srt.query.DataAccessTaskQuery;
import net.srt.service.DataAccessTaskService;
import net.srt.vo.DataAccessTaskVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 数据接入任务记录
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-10-24
 */
@Service
@AllArgsConstructor
public class DataAccessTaskServiceImpl extends BaseServiceImpl<DataAccessTaskDao, DataAccessTaskEntity> implements DataAccessTaskService {

	@Override
	public PageResult<DataAccessTaskVO> page(DataAccessTaskQuery query) {
		IPage<DataAccessTaskEntity> page = baseMapper.selectPage(getPage(query), getWrapper(query));

		return new PageResult<>(DataAccessTaskConvert.INSTANCE.convertList(page.getRecords()), page.getTotal());
	}

	private LambdaQueryWrapper<DataAccessTaskEntity> getWrapper(DataAccessTaskQuery query) {
		LambdaQueryWrapper<DataAccessTaskEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(DataAccessTaskEntity::getDataAccessId, query.getDataAccessId());
		wrapper.eq(query.getRunStatus() != null, DataAccessTaskEntity::getRunStatus, query.getRunStatus());
		wrapper.orderByDesc(DataAccessTaskEntity::getCreateTime);
		wrapper.orderByDesc(DataAccessTaskEntity::getId);
		return wrapper;
	}

	@Override
	public void save(DataAccessTaskVO vo) {
		DataAccessTaskEntity entity = DataAccessTaskConvert.INSTANCE.convert(vo);

		baseMapper.insert(entity);
	}

	@Override
	public void update(DataAccessTaskVO vo) {
		DataAccessTaskEntity entity = DataAccessTaskConvert.INSTANCE.convert(vo);

		updateById(entity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(List<Long> idList) {
		removeByIds(idList);
	}

	@Override
	public void deleteByAccessId(Long id) {
		LambdaQueryWrapper<DataAccessTaskEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(DataAccessTaskEntity::getDataAccessId, id);
		remove(wrapper);
	}

	@Override
	public void dealNotFinished() {
		LambdaQueryWrapper<DataAccessTaskEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.in(DataAccessTaskEntity::getRunStatus, CommonRunStatus.WAITING.getCode(), CommonRunStatus.RUNNING.getCode());
		List<DataAccessTaskEntity> accessTaskEntities = baseMapper.selectList(wrapper);
		for (DataAccessTaskEntity accessTaskEntity : accessTaskEntities) {
			accessTaskEntity.setEndTime(new Date());
			accessTaskEntity.setRunStatus(CommonRunStatus.FAILED.getCode());
			String errorLog = DateUtils.formatDateTime(new Date()) + " The sync task has unexpected stop,you can try run again";
			accessTaskEntity.setErrorInfo(accessTaskEntity.getErrorInfo() == null ? errorLog : accessTaskEntity.getErrorInfo() + "\r\n" + errorLog);
			baseMapper.updateById(accessTaskEntity);
		}
	}

}
