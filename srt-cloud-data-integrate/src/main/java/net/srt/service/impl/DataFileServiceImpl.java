package net.srt.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import net.srt.convert.DataFileConvert;
import net.srt.dao.DataFileCategoryDao;
import net.srt.entity.DataFileCategoryEntity;
import net.srt.entity.DataFileEntity;
import net.srt.framework.common.constant.Constant;
import net.srt.framework.common.page.PageResult;
import net.srt.framework.mybatis.service.impl.BaseServiceImpl;
import net.srt.framework.security.user.SecurityUser;
import net.srt.query.DataFileQuery;
import net.srt.vo.DataFileVO;
import net.srt.dao.DataFileDao;
import net.srt.service.DataFileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import srt.cloud.framework.dbswitch.common.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件表
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-11-16
 */
@Service
@AllArgsConstructor
public class DataFileServiceImpl extends BaseServiceImpl<DataFileDao, DataFileEntity> implements DataFileService {


	private final DataFileCategoryDao dataFileCategoryDao;

	@Override
	public PageResult<DataFileVO> page(DataFileQuery query) {
		IPage<DataFileEntity> page = baseMapper.selectPage(getPage(query), getWrapper(query));

		return new PageResult<>(DataFileConvert.INSTANCE.convertList(page.getRecords()), page.getTotal());
	}

	@Override
	public PageResult<DataFileVO> pageResource(DataFileQuery query) {
		// 查询参数
		Map<String, Object> params = getParams(query);
		IPage<DataFileEntity> page = getPage(query);
		params.put(Constant.PAGE, page);
		// 数据列表
		List<DataFileEntity> list = baseMapper.getResourceList(params);
		List<DataFileVO> dataFileVOS = DataFileConvert.INSTANCE.convertList(list);
		for (DataFileVO dataFileVO : dataFileVOS) {
			DataFileCategoryEntity categoryEntity = dataFileCategoryDao.selectById(dataFileVO.getFileCategoryId());
			dataFileVO.setGroup(categoryEntity != null ? categoryEntity.getPath() : null);
		}
		return new PageResult<>(dataFileVOS, page.getTotal());
	}

	private Map<String, Object> getParams(DataFileQuery query) {
		Map<String, Object> params = new HashMap<>();
		params.put("queryApply", query.getQueryApply());
		if (query.getQueryApply() != null && query.getQueryApply() == 1) {
			params.put("userId", SecurityUser.getUserId());
		}
		params.put("resourceId", query.getResourceId());
		params.put("fileCategoryId", query.getFileCategoryId());
		params.put("type", query.getType());
		params.put("name", query.getName());
		return params;
	}

	private LambdaQueryWrapper<DataFileEntity> getWrapper(DataFileQuery query) {
		LambdaQueryWrapper<DataFileEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.like(StringUtil.isNotBlank(query.getName()), DataFileEntity::getName, query.getName());
		wrapper.like(StringUtil.isNotBlank(query.getType()), DataFileEntity::getType, query.getType());
		wrapper.eq(query.getFileCategoryId() != null, DataFileEntity::getFileCategoryId, query.getFileCategoryId());
		return wrapper;
	}

	@Override
	public void save(DataFileVO vo) {
		DataFileCategoryEntity categoryEntity = dataFileCategoryDao.selectById(vo.getFileCategoryId());
		DataFileEntity entity = DataFileConvert.INSTANCE.convert(vo);
		entity.setProjectId(getProjectId());
		entity.setOrgId(categoryEntity.getOrgId());
		baseMapper.insert(entity);
	}

	@Override
	public void update(DataFileVO vo) {
		DataFileCategoryEntity categoryEntity = dataFileCategoryDao.selectById(vo.getFileCategoryId());
		DataFileEntity entity = DataFileConvert.INSTANCE.convert(vo);
		entity.setProjectId(getProjectId());
		entity.setOrgId(categoryEntity.getOrgId());
		updateById(entity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(List<Long> idList) {
		removeByIds(idList);
	}

}
