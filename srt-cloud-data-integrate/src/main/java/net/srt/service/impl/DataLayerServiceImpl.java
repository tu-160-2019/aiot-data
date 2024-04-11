package net.srt.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import net.srt.convert.DataLayerConvert;
import net.srt.entity.DataLayerEntity;
import net.srt.framework.common.page.PageResult;
import net.srt.framework.mybatis.service.impl.BaseServiceImpl;
import net.srt.query.DataLayerQuery;
import net.srt.vo.DataLayerVO;
import net.srt.dao.DataLayerDao;
import net.srt.service.DataLayerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 数仓分层
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-10-08
 */
@Service
@AllArgsConstructor
public class DataLayerServiceImpl extends BaseServiceImpl<DataLayerDao, DataLayerEntity> implements DataLayerService {

	@Override
	public PageResult<DataLayerVO> page(DataLayerQuery query) {
		IPage<DataLayerEntity> page = baseMapper.selectPage(getPage(query), getWrapper(query));

		return new PageResult<>(DataLayerConvert.INSTANCE.convertList(page.getRecords()), page.getTotal());
	}

	private LambdaQueryWrapper<DataLayerEntity> getWrapper(DataLayerQuery query) {
		LambdaQueryWrapper<DataLayerEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.like(StrUtil.isNotBlank(query.getCnName()), DataLayerEntity::getCnName, query.getCnName());
		wrapper.like(StrUtil.isNotBlank(query.getName()), DataLayerEntity::getName, query.getName());
		return wrapper;
	}

	@Override
	public void save(DataLayerVO vo) {
		DataLayerEntity entity = DataLayerConvert.INSTANCE.convert(vo);

		baseMapper.insert(entity);
	}

	@Override
	public void update(DataLayerVO vo) {
		DataLayerEntity entity = DataLayerConvert.INSTANCE.convert(vo);

		updateById(entity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(List<Long> idList) {
		removeByIds(idList);
	}

}
