package net.srt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import net.srt.convert.DataProjectUserRelConvert;
import net.srt.dao.DataProjectUserRelDao;
import net.srt.entity.DataProjectEntity;
import net.srt.entity.DataProjectUserRelEntity;
import net.srt.framework.mybatis.service.impl.BaseServiceImpl;
import net.srt.service.DataProjectUserRelService;
import net.srt.vo.DataProjectUserRelVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.BaseRowSet;
import java.text.BreakIterator;
import java.util.List;

/**
 * 项目用户关联表
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-10-08
 */
@Service
@AllArgsConstructor
public class DataProjectUserRelServiceImpl extends BaseServiceImpl<DataProjectUserRelDao, DataProjectUserRelEntity> implements DataProjectUserRelService {


	@Override
	public void save(DataProjectUserRelVO vo) {
		DataProjectUserRelEntity entity = DataProjectUserRelConvert.INSTANCE.convert(vo);

		baseMapper.insert(entity);
	}

	@Override
	public void update(DataProjectUserRelVO vo) {
		DataProjectUserRelEntity entity = DataProjectUserRelConvert.INSTANCE.convert(vo);

		updateById(entity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(List<Long> idList) {
		removeByIds(idList);
	}

	@Override
	public DataProjectUserRelEntity getByProjectIdAndUserId(Long projectId, Long userId) {
		return baseMapper.getByProjectIdAndUserId(projectId, userId);
	}

}
