package net.srt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import net.srt.constants.DataHouseLayer;
import net.srt.convert.DataOdsConvert;
import net.srt.dao.DataTableDao;
import net.srt.entity.DataTableEntity;
import net.srt.framework.common.cache.bean.DataProjectCacheBean;
import net.srt.framework.common.page.PageResult;
import net.srt.framework.common.utils.BeanUtil;
import net.srt.framework.common.utils.SqlUtils;
import net.srt.framework.mybatis.service.impl.BaseServiceImpl;
import net.srt.query.DataTableQuery;
import net.srt.service.DataTableService;
import net.srt.vo.ColumnDescriptionVo;
import net.srt.vo.DataTableVO;
import net.srt.vo.SchemaTableDataVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.util.StringUtil;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;
import srt.cloud.framework.dbswitch.core.model.SchemaTableData;
import srt.cloud.framework.dbswitch.core.model.TableDescription;
import srt.cloud.framework.dbswitch.core.service.IMetaDataByJdbcService;
import srt.cloud.framework.dbswitch.core.service.impl.MetaDataByJdbcServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据集成-贴源数据
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-11-07
 */
@Service
@AllArgsConstructor
public class DataTableServiceImpl extends BaseServiceImpl<DataTableDao, DataTableEntity> implements DataTableService {

	@Override
	public PageResult<DataTableVO> page(DataTableQuery query) {
		/*IPage<DataOdsEntity> page = baseMapper.selectPage(getPage(query), getWrapper(query));
		return new PageResult<>(DataOdsConvert.INSTANCE.convertList(page.getRecords()), page.getTotal());*/
		//获取该项目下的所有表
		DataProjectCacheBean project = getProject();
		IMetaDataByJdbcService metaDataService = new MetaDataByJdbcServiceImpl(ProductTypeEnum.getByIndex(project.getDbType()));
		List<TableDescription> tableList = metaDataService.queryTableList(project.getDbUrl(), project.getDbUsername(), project.getDbPassword(), project.getDbSchema())
				.stream().filter(item -> item.getTableName().startsWith(DataHouseLayer.ODS.getTablePrefix()))
				.filter(item -> !StringUtil.isNotBlank(query.getTableName()) || item.getTableName().contains(query.getTableName()))
				.filter(item -> !StringUtil.isNotBlank(query.getRemarks()) || item.getRemarks().contains(query.getRemarks()))
				.collect(Collectors.toList());

		int startIndex = (query.getPage() - 1) * query.getLimit();
		int endIndex = Math.min(query.getPage() * query.getLimit(), tableList.size());
		List<TableDescription> pageList = tableList.subList(startIndex, endIndex);
		List<DataTableVO> dataTableVOS = new ArrayList<>(10);
		for (TableDescription tableDescription : pageList) {
			DataTableVO dataTableVO = new DataTableVO();
			dataTableVO.setTableName(tableDescription.getTableName());
			dataTableVO.setRemarks(tableDescription.getRemarks());
			dataTableVO.setProjectId(project.getId());
			//查询data_ods,补充数据
			LambdaQueryWrapper<DataTableEntity> wrapper = Wrappers.lambdaQuery();
			wrapper.eq(DataTableEntity::getTableName, dataTableVO.getTableName()).eq(DataTableEntity::getProjectId, dataTableVO.getProjectId()).last("LIMIT 1");
			DataTableEntity dataTableEntity = baseMapper.selectOne(wrapper);
			if (dataTableEntity != null) {
				//说明是通过数据接入接入的
				dataTableVO.setDataAccessId(dataTableEntity.getDataAccessId());
				dataTableVO.setRecentlySyncTime(dataTableEntity.getRecentlySyncTime());
			}
			dataTableVOS.add(dataTableVO);
		}
		return new PageResult<>(dataTableVOS, tableList.size());
	}

	/*private LambdaQueryWrapper<DataOdsEntity> getWrapper(DataOdsQuery query) {
		LambdaQueryWrapper<DataOdsEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.like(StringUtil.isNotBlank(query.getTableName()), DataOdsEntity::getTableName, query.getTableName());
		wrapper.like(StringUtil.isNotBlank(query.getRemarks()), DataOdsEntity::getRemarks, query.getRemarks());
		wrapper.eq(query.getProjectId() != null, DataOdsEntity::getProjectId, query.getProjectId());
		dataScopeWithoutOrgId(wrapper);
		return wrapper;
	}*/

	@Override
	public void save(DataTableVO vo) {
		DataTableEntity entity = DataOdsConvert.INSTANCE.convert(vo);

		baseMapper.insert(entity);
	}

	@Override
	public void update(DataTableVO vo) {
		DataTableEntity entity = DataOdsConvert.INSTANCE.convert(vo);

		updateById(entity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(List<Long> idList) {
		removeByIds(idList);
	}

	@Override
	public DataTableEntity getByTableName(Long projectId, String tableName) {
		LambdaQueryWrapper<DataTableEntity> wrapper = new LambdaQueryWrapper<>();
		return baseMapper.selectOne(wrapper.eq(DataTableEntity::getTableName, tableName).eq(DataTableEntity::getProjectId, projectId));
	}

	@Override
	public List<ColumnDescriptionVo> getColumnInfo(String tableName) {
		//DataOdsEntity dataOdsEntity = baseMapper.selectById(id);
		DataProjectCacheBean project = getProject();
		IMetaDataByJdbcService service = new MetaDataByJdbcServiceImpl(ProductTypeEnum.getByIndex(project.getDbType()));
		List<ColumnDescription> columnDescriptions = service.queryTableColumnMeta(project.getDbUrl(), project.getDbUsername(), project.getDbPassword(), project.getDbSchema(), tableName);
		List<String> pks = service.queryTablePrimaryKeys(project.getDbUrl(), project.getDbUsername(), project.getDbPassword(), project.getDbSchema(), tableName);
		return BeanUtil.copyListProperties(columnDescriptions, ColumnDescriptionVo::new, (oldItem, newItem) -> {
			newItem.setFieldName(StringUtil.isNotBlank(newItem.getFieldName()) ? newItem.getFieldName() : newItem.getLabelName());
			if (pks.contains(newItem.getFieldName())) {
				newItem.setPk(true);
			}
		});
	}

	@Override
	public SchemaTableDataVo getTableData(String tableName) {
		DataProjectCacheBean project = getProject();
		IMetaDataByJdbcService service = new MetaDataByJdbcServiceImpl(ProductTypeEnum.getByIndex(project.getDbType()));
		SchemaTableData schemaTableData = service.queryTableData(project.getDbUrl(), project.getDbUsername(), project.getDbPassword(), project.getDbSchema(), tableName, 50);
		return SchemaTableDataVo.builder().columns(SqlUtils.convertColumns(schemaTableData.getColumns())).rows(SqlUtils.convertRows(schemaTableData.getColumns(), schemaTableData.getRows())).build();
	}
}
