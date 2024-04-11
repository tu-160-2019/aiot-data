package net.srt.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import net.srt.convert.DataAccessTaskDetailConvert;
import net.srt.entity.DataAccessTaskDetailEntity;
import net.srt.framework.common.page.PageResult;
import net.srt.framework.mybatis.service.impl.BaseServiceImpl;
import net.srt.query.DataAccessTaskDetailQuery;
import net.srt.vo.DataAccessTaskDetailVO;
import net.srt.dao.DataAccessTaskDetailDao;
import net.srt.service.DataAccessTaskDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import srt.cloud.framework.dbswitch.common.util.StringUtil;

import java.util.List;

/**
 * 数据接入-同步记录详情
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-10-28
 */
@Service
@AllArgsConstructor
public class DataAccessTaskDetailServiceImpl extends BaseServiceImpl<DataAccessTaskDetailDao, DataAccessTaskDetailEntity> implements DataAccessTaskDetailService {

	@Override
	public PageResult<DataAccessTaskDetailVO> page(DataAccessTaskDetailQuery query) {
		IPage<DataAccessTaskDetailEntity> page = baseMapper.selectPage(getPage(query), getWrapper(query));

		return new PageResult<>(DataAccessTaskDetailConvert.INSTANCE.convertList(page.getRecords()), page.getTotal());
	}

	private LambdaQueryWrapper<DataAccessTaskDetailEntity> getWrapper(DataAccessTaskDetailQuery query) {
		LambdaQueryWrapper<DataAccessTaskDetailEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(query.getIfSuccess() != null, DataAccessTaskDetailEntity::getIfSuccess, query.getIfSuccess());
		wrapper.eq(query.getProjectId() != null, DataAccessTaskDetailEntity::getProjectId, query.getProjectId());
		wrapper.eq(query.getTaskId() != null, DataAccessTaskDetailEntity::getTaskId, query.getTaskId());
		wrapper.eq(StringUtil.isNotBlank(query.getTableName()), DataAccessTaskDetailEntity::getTargetTableName, query.getTableName());
		wrapper.orderByDesc(DataAccessTaskDetailEntity::getCreateTime);
		wrapper.orderByDesc(DataAccessTaskDetailEntity::getId);
		return wrapper;
	}

	@Override
	public void save(DataAccessTaskDetailVO vo) {
		DataAccessTaskDetailEntity entity = DataAccessTaskDetailConvert.INSTANCE.convert(vo);

		baseMapper.insert(entity);
	}

	@Override
	public void update(DataAccessTaskDetailVO vo) {
		DataAccessTaskDetailEntity entity = DataAccessTaskDetailConvert.INSTANCE.convert(vo);

		updateById(entity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(List<Long> idList) {
		removeByIds(idList);
	}

	@Override
	public void deleteByTaskId(List<Long> idList) {
		idList.forEach(taskId -> {
			LambdaQueryWrapper<DataAccessTaskDetailEntity> wrapper = new LambdaQueryWrapper<>();
			wrapper.eq(DataAccessTaskDetailEntity::getTaskId, taskId);
			remove(wrapper);
		});
	}

	@Override
	public void deleteByAccessId(Long id) {
		LambdaQueryWrapper<DataAccessTaskDetailEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(DataAccessTaskDetailEntity::getDataAccessId, id);
		remove(wrapper);
	}

}
