package net.srt.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import net.srt.convert.DataFileCategoryConvert;
import net.srt.dao.DataFileCategoryDao;
import net.srt.entity.DataFileCategoryEntity;
import net.srt.entity.DataFileEntity;
import net.srt.framework.common.exception.ServerException;
import net.srt.framework.common.utils.BeanUtil;
import net.srt.framework.common.utils.BuildTreeUtils;
import net.srt.framework.common.utils.TreeNodeVo;
import net.srt.framework.mybatis.service.impl.BaseServiceImpl;
import net.srt.service.DataFileCategoryService;
import net.srt.service.DataFileService;
import net.srt.vo.DataFileCategoryVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import srt.cloud.framework.dbswitch.common.util.StringUtil;

import java.util.List;

/**
 * 文件分组表
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-11-12
 */
@Service
@AllArgsConstructor
public class DataFileCategoryServiceImpl extends BaseServiceImpl<DataFileCategoryDao, DataFileCategoryEntity> implements DataFileCategoryService {

	private final DataFileService dataFileService;

	@Override
	public void save(DataFileCategoryVO vo) {
		DataFileCategoryEntity entity = DataFileCategoryConvert.INSTANCE.convert(vo);
		entity.setPath(recursionPath(entity, null));
		entity.setProjectId(getProjectId());
		baseMapper.insert(entity);
	}

	@Override
	public void update(DataFileCategoryVO vo) {
		DataFileCategoryEntity entity = DataFileCategoryConvert.INSTANCE.convert(vo);
		entity.setPath(recursionPath(entity, null));
		entity.setProjectId(getProjectId());
		updateById(entity);
		//更新文件的orgId
		LambdaUpdateWrapper<DataFileEntity> wrapper = Wrappers.lambdaUpdate();
		wrapper.eq(DataFileEntity::getFileCategoryId, vo.getId());
		wrapper.set(DataFileEntity::getOrgId, vo.getOrgId());
		dataFileService.update(wrapper);
	}

	private String recursionPath(DataFileCategoryEntity categoryEntity, String path) {
		if (StringUtil.isBlank(path)) {
			path = categoryEntity.getName();
		}
		if (categoryEntity.getParentId() != 0) {
			DataFileCategoryEntity parent = getById(categoryEntity.getParentId());
			path = parent.getName() + "/" + path;
			return recursionPath(parent, path);
		}
		return path;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(Long id) {
		//查询有没有子节点
		LambdaQueryWrapper<DataFileCategoryEntity> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(DataFileCategoryEntity::getParentId, id).last(" limit 1");
		DataFileCategoryEntity one = baseMapper.selectOne(wrapper);
		if (one != null) {
			throw new ServerException("存在子节点，不允许删除！");
		}
		//查询有没有文件与之关联
		LambdaQueryWrapper<DataFileEntity> fileEntityLambdaQueryWrapper = new LambdaQueryWrapper<>();
		fileEntityLambdaQueryWrapper.eq(DataFileEntity::getFileCategoryId, id).last(" limit 1");
		DataFileEntity dataFileEntity = dataFileService.getOne(fileEntityLambdaQueryWrapper);
		if (dataFileEntity != null) {
			throw new ServerException("节点下有文件，不允许删除！");
		}
		removeById(id);
	}

	@Override
	public List<TreeNodeVo> listTree() {
		LambdaQueryWrapper<DataFileCategoryEntity> wrapper = new LambdaQueryWrapper<>();
		dataScopeWithOrgId(wrapper);
		wrapper.orderByAsc(DataFileCategoryEntity::getOrderNo);
		List<DataFileCategoryEntity> dataFileCategoryEntities = baseMapper.selectList(wrapper);
		List<TreeNodeVo> treeNodeVos = BeanUtil.copyListProperties(dataFileCategoryEntities, TreeNodeVo::new, (oldItem, newItem) -> {
			newItem.setLabel(oldItem.getName());
			newItem.setValue(oldItem.getId());
			newItem.setDisabled(oldItem.getType() == 0);
			if (newItem.getPath().contains("/")) {
				newItem.setParentPath(newItem.getPath().substring(0, newItem.getPath().lastIndexOf("/")));
			}
		});
		return BuildTreeUtils.buildTree(treeNodeVos);
	}

}
