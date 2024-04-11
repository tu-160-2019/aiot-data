package net.srt.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.srt.api.module.data.development.DataProductionTaskApi;
import net.srt.api.module.data.development.dto.DataProductionTaskDto;
import net.srt.api.module.data.governance.DataMetadataCollectApi;
import net.srt.api.module.data.governance.dto.DataGovernanceMetadataCollectDto;
import net.srt.api.module.data.governance.dto.DataGovernanceMetadataDto;
import net.srt.constants.DataHouseLayer;
import net.srt.constants.MiddleTreeNodeType;
import net.srt.constants.YesOrNo;
import net.srt.convert.DataDatabaseConvert;
import net.srt.dao.DataAccessDao;
import net.srt.dao.DataDatabaseDao;
import net.srt.dto.ColumnInfo;
import net.srt.dto.SqlConsole;
import net.srt.dto.TableInfo;
import net.srt.entity.DataAccessEntity;
import net.srt.entity.DataDatabaseEntity;
import net.srt.flink.common.utils.LogUtil;
import net.srt.framework.common.cache.bean.DataProjectCacheBean;
import net.srt.framework.common.exception.ServerException;
import net.srt.framework.common.page.PageResult;
import net.srt.framework.common.utils.BeanUtil;
import net.srt.framework.common.utils.SqlUtils;
import net.srt.framework.common.utils.TreeNodeVo;
import net.srt.framework.mybatis.service.impl.BaseServiceImpl;
import net.srt.query.DataDatabaseQuery;
import net.srt.service.DataAccessService;
import net.srt.service.DataDatabaseService;
import net.srt.vo.ColumnDescriptionVo;
import net.srt.vo.DataDatabaseVO;
import net.srt.vo.SchemaTableDataVo;
import net.srt.vo.SqlGenerationVo;
import net.srt.vo.TableVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.util.StringUtil;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;
import srt.cloud.framework.dbswitch.core.model.SchemaTableData;
import srt.cloud.framework.dbswitch.core.model.TableDescription;
import srt.cloud.framework.dbswitch.core.service.IMetaDataByJdbcService;
import srt.cloud.framework.dbswitch.core.service.impl.MetaDataByJdbcServiceImpl;
import srt.cloud.framework.dbswitch.core.util.GenerateSqlUtils;
import srt.cloud.framework.dbswitch.data.entity.TargetDataSourceProperties;
import srt.cloud.framework.dbswitch.data.util.DataSourceUtils;
import srt.cloud.framework.dbswitch.dbcommon.database.DatabaseOperatorFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据集成-数据库管理
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-10-09
 */
@Service
@AllArgsConstructor
public class DataDatabaseServiceImpl extends BaseServiceImpl<DataDatabaseDao, DataDatabaseEntity> implements DataDatabaseService {

	private final DataAccessDao dataAccessDao;
	private final DataAccessService dataAccessService;
	private final DataProductionTaskApi productionTaskApi;
	private final DataMetadataCollectApi dataMetadataCollectApi;

	@Override
	public PageResult<DataDatabaseVO> page(DataDatabaseQuery query) {
		IPage<DataDatabaseEntity> page = baseMapper.selectPage(getPage(query), getWrapper(query));

		return new PageResult<>(DataDatabaseConvert.INSTANCE.convertList(page.getRecords()), page.getTotal());
	}

	private LambdaQueryWrapper<DataDatabaseEntity> getWrapper(DataDatabaseQuery query) {
		LambdaQueryWrapper<DataDatabaseEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.like(StrUtil.isNotBlank(query.getName()), DataDatabaseEntity::getName, query.getName());
		wrapper.like(StrUtil.isNotBlank(query.getDatabaseName()), DataDatabaseEntity::getDatabaseName, query.getDatabaseName());
		wrapper.like(StrUtil.isNotBlank(query.getDatabaseSchema()), DataDatabaseEntity::getDatabaseSchema, query.getDatabaseSchema());
		wrapper.eq(query.getDatabaseType() != null, DataDatabaseEntity::getDatabaseType, query.getDatabaseType());
		wrapper.eq(query.getStatus() != null, DataDatabaseEntity::getStatus, query.getStatus());
		wrapper.eq(query.getIsRtApprove() != null, DataDatabaseEntity::getIsRtApprove, query.getIsRtApprove());
		dataScopeWithOrgId(wrapper);
		return wrapper;
	}

	@Override
	public void save(DataDatabaseVO vo) {
		DataDatabaseEntity entity = DataDatabaseConvert.INSTANCE.convert(vo);
		entity.setProjectId(getProjectId());
		setJdbcUrlByEntity(entity);
		baseMapper.insert(entity);
		try {
			testOnline(DataDatabaseConvert.INSTANCE.convert(entity));
		} catch (Exception ignored) {
		}

	}

	@Override
	public void update(DataDatabaseVO vo) {
		DataDatabaseEntity entity = DataDatabaseConvert.INSTANCE.convert(vo);
		LambdaQueryWrapper<DataAccessEntity> dataAccessEntityWrapper = new LambdaQueryWrapper<>();
		dataAccessEntityWrapper.eq(DataAccessEntity::getSourceDatabaseId, vo.getId()).or().eq(DataAccessEntity::getTargetDatabaseId, vo.getId());
		setJdbcUrlByEntity(entity);
		entity.setProjectId(getProjectId());
		List<DataAccessEntity> dataAccessEntities = dataAccessDao.selectList(dataAccessEntityWrapper);
		for (DataAccessEntity dataAccessEntity : dataAccessEntities) {
			//修改数据库的同时，同时修改一下相关的数据接入任务
			dataAccessService.update(dataAccessService.getById(dataAccessEntity.getId()));
		}
		updateById(entity);
		try {
			testOnline(DataDatabaseConvert.INSTANCE.convert(entity));
		} catch (Exception ignored) {
		}
	}

	private void setJdbcUrlByEntity(DataDatabaseEntity entity) {
		ProductTypeEnum productTypeEnum = ProductTypeEnum.getByIndex(entity.getDatabaseType());
		entity.setJdbcUrl(StringUtil.isBlank(entity.getJdbcUrl()) ? productTypeEnum.getUrl()
				.replace("{host}", entity.getDatabaseIp())
				.replace("{port}", entity.getDatabasePort())
				.replace("{database}", entity.getDatabaseName()) : entity.getJdbcUrl());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(List<Long> idList) {
		LambdaQueryWrapper<DataAccessEntity> dataAccessEntityWrapper = new LambdaQueryWrapper<>();
		dataAccessEntityWrapper.in(DataAccessEntity::getSourceDatabaseId, idList).or().in(DataAccessEntity::getTargetDatabaseId, idList).last(" limit 1");
		DataAccessEntity dataAccessEntity = dataAccessDao.selectOne(dataAccessEntityWrapper);
		if (dataAccessEntity != null) {
			throw new ServerException(String.format("要删除的数据库中有数据接入任务【%s】与之关联，不允许删除！", dataAccessEntity.getTaskName()));
		}
		for (Long id : idList) {
			DataProductionTaskDto task = productionTaskApi.getByDbId(id).getData();
			if (task != null) {
				throw new ServerException(String.format("删除的数据中有数据生产任务【%s】与之关联，不允许删除！", task.getName()));
			}
			DataGovernanceMetadataCollectDto metadataCollectDto = dataMetadataCollectApi.getByDatasourceId(id).getData();
			if (metadataCollectDto != null) {
				throw new ServerException(String.format("删除的数据中有数据采集任务【%s】与之关联，不允许删除！", metadataCollectDto.getName()));
			}
			DataGovernanceMetadataDto metadataDto = dataMetadataCollectApi.getMetadataByDatasourceId(id).getData();
			if (metadataDto != null) {
				throw new ServerException(String.format("删除的数据中有元数据【%s】与之关联，不允许删除！", metadataDto.getName()));
			}
		}
		removeByIds(idList);
	}

	@Override
	public void testOnline(DataDatabaseVO vo) {
		ProductTypeEnum productTypeEnum = ProductTypeEnum.getByIndex(vo.getDatabaseType());
		IMetaDataByJdbcService metaDataService = new MetaDataByJdbcServiceImpl(productTypeEnum);
		if (StringUtil.isBlank(vo.getJdbcUrl())) {
			vo.setJdbcUrl(productTypeEnum.getUrl()
					.replace("{host}", vo.getDatabaseIp())
					.replace("{port}", vo.getDatabasePort())
					.replace("{database}", vo.getDatabaseName()));
		}
		metaDataService.testQuerySQL(
				vo.getJdbcUrl(),
				vo.getUserName(),
				vo.getPassword(),
				productTypeEnum.getTestSql()
		);
		if (vo.getId() != null) {
			//更新状态
			baseMapper.changeStatusById(vo.getId(), YesOrNo.YES.getValue());
		}
	}

	@Override
	public List<TableVo> getTablesById(Long id) {
		DataDatabaseEntity dataDatabaseEntity;
		if (id == 0) {
			dataDatabaseEntity = buildMiddleEntity();
		} else {
			dataDatabaseEntity = baseMapper.selectById(id);
		}
		return getTables(dataDatabaseEntity);
	}

	private List<TableVo> getTables(DataDatabaseEntity dataDatabaseEntity) {
		ProductTypeEnum productTypeEnum = ProductTypeEnum.getByIndex(dataDatabaseEntity.getDatabaseType());
		IMetaDataByJdbcService metaDataService = new MetaDataByJdbcServiceImpl(productTypeEnum);
		List<TableDescription> tableDescriptions = metaDataService.queryTableList(StringUtil.isBlank(dataDatabaseEntity.getJdbcUrl()) ? productTypeEnum.getUrl()
						.replace("{host}", dataDatabaseEntity.getDatabaseIp())
						.replace("{port}", dataDatabaseEntity.getDatabasePort())
						.replace("{database}", dataDatabaseEntity.getDatabaseName()) : dataDatabaseEntity.getJdbcUrl(), dataDatabaseEntity.getUserName(), dataDatabaseEntity.getPassword(),
				dataDatabaseEntity.getDatabaseSchema());
		if (dataDatabaseEntity.getId() == null) {
			//中台库返回ods层的表
			return BeanUtil.copyListProperties(tableDescriptions, TableVo::new).stream().filter(item -> item.getTableName().startsWith(DataHouseLayer.ODS.getTablePrefix())).collect(Collectors.toList());
		}
		return BeanUtil.copyListProperties(tableDescriptions, TableVo::new);
	}

	@SneakyThrows
	@Override
	public SchemaTableDataVo getTableDataBySql(Integer id, SqlConsole sqlConsole) {
		DataDatabaseEntity dataDatabaseEntity = baseMapper.selectById(id);
		if (!ProductTypeEnum.MONGODB.getIndex().equals(dataDatabaseEntity.getDatabaseType())) {
			Statement parse = CCJSqlParserUtil.parse(sqlConsole.getSql());
			if (!(parse instanceof Select)) {
				throw new ServerException("只能执行select查询语句！");
			}
		}
		ProductTypeEnum productTypeEnum = ProductTypeEnum.getByIndex(dataDatabaseEntity.getDatabaseType());
		IMetaDataByJdbcService metaDataService = new MetaDataByJdbcServiceImpl(productTypeEnum);
		SchemaTableData schemaTableData = metaDataService.queryTableDataBySql(StringUtil.isBlank(dataDatabaseEntity.getJdbcUrl()) ? productTypeEnum.getUrl()
				.replace("{host}", dataDatabaseEntity.getDatabaseIp())
				.replace("{port}", dataDatabaseEntity.getDatabasePort())
				.replace("{database}", dataDatabaseEntity.getDatabaseName()) : dataDatabaseEntity.getJdbcUrl(), dataDatabaseEntity.getUserName(), dataDatabaseEntity.getPassword(), sqlConsole.getSql(), 100);
		return SchemaTableDataVo.builder().columns(SqlUtils.convertColumns(schemaTableData.getColumns())).rows(SqlUtils.convertRows(schemaTableData.getColumns(), schemaTableData.getRows())).build();
	}

	@Override
	public List<DataDatabaseVO> listAll() {
		LambdaQueryWrapper<DataDatabaseEntity> wrapper = Wrappers.lambdaQuery();
		dataScopeWithOrgId(wrapper);
		return DataDatabaseConvert.INSTANCE.convertList(baseMapper.selectList(wrapper));
	}

	@Override
	public List<TreeNodeVo> listTree(Long id) {
		DataDatabaseEntity entity = baseMapper.selectById(id);
		setJdbcUrlByEntity(entity);
		List<TableVo> tables = getTables(entity);
		List<TreeNodeVo> nodeList = new ArrayList<>(1);
		TreeNodeVo dbNode = new TreeNodeVo();
		nodeList.add(dbNode);
		dbNode.setName(entity.getName());
		dbNode.setDescription(entity.getName());
		dbNode.setLabel(entity.getDatabaseName());
		dbNode.setId(entity.getId());
		dbNode.setIfLeaf(YesOrNo.YES.getValue());
		dbNode.setAttributes(entity);
		List<TreeNodeVo> tableNodes = new ArrayList<>(10);
		dbNode.setChildren(tableNodes);
		for (TableVo table : tables) {
			TreeNodeVo tableNode = new TreeNodeVo();
			tableNode.setLabel(table.getTableName());
			tableNode.setName(table.getTableName());
			tableNode.setDescription(table.getRemarks());
			tableNode.setIfLeaf(YesOrNo.NO.getValue());
			tableNodes.add(tableNode);
		}
		return nodeList;
	}

	@Override
	public List<ColumnDescriptionVo> getColumnInfo(Long id, String tableName) {
		DataDatabaseEntity entity = baseMapper.selectById(id);
		return getColumnDescriptionVos(tableName, entity);
	}

	@Override
	public List<ColumnDescriptionVo> getColumnInfoBySql(Long id, SqlConsole sqlConsole) {
		DataDatabaseEntity entity = baseMapper.selectById(id);
		setJdbcUrlByEntity(entity);
		ProductTypeEnum productTypeEnum = ProductTypeEnum.getByIndex(entity.getDatabaseType());
		IMetaDataByJdbcService service = new MetaDataByJdbcServiceImpl(productTypeEnum);
		try {
			List<ColumnDescription> columnDescriptions = service.querySqlColumnMeta(entity.getJdbcUrl(), entity.getUserName(), entity.getPassword(), sqlConsole.getSql());
			return BeanUtil.copyListProperties(columnDescriptions, ColumnDescriptionVo::new, (oldItem, newItem) -> newItem.setFieldName(StringUtil.isNotBlank(newItem.getFieldName()) ? newItem.getFieldName() : newItem.getLabelName()));
		} catch (Exception e) {
			throw new ServerException("SQL 有误，请检查！请填写单条 SELECT 语句！");
		}

	}

	private List<ColumnDescriptionVo> getColumnDescriptionVos(String tableName, DataDatabaseEntity entity) {
		setJdbcUrlByEntity(entity);
		ProductTypeEnum productTypeEnum = ProductTypeEnum.getByIndex(entity.getDatabaseType());
		IMetaDataByJdbcService service = new MetaDataByJdbcServiceImpl(productTypeEnum);
		List<ColumnDescription> columnDescriptions = service.queryTableColumnMeta(entity.getJdbcUrl(), entity.getUserName(), entity.getPassword(), entity.getDatabaseSchema(), tableName);
		List<String> pks = service.queryTablePrimaryKeys(entity.getJdbcUrl(), entity.getUserName(), entity.getPassword(), entity.getDatabaseSchema(), tableName);
		return BeanUtil.copyListProperties(columnDescriptions, ColumnDescriptionVo::new, (oldItem, newItem) -> {
			newItem.setFieldName(StringUtil.isNotBlank(newItem.getFieldName()) ? newItem.getFieldName() : newItem.getLabelName());
			if (pks.contains(newItem.getFieldName())) {
				newItem.setPk(true);
			}
		});
	}

	@Override
	public SqlGenerationVo getSqlGeneration(Long id, String tableName, String tableRemarks) {
		DataDatabaseEntity entity = baseMapper.selectById(id);
		return getSqlGenerationVo(tableName, tableRemarks, entity);
	}

	private SqlGenerationVo getSqlGenerationVo(String tableName, String tableRemarks, DataDatabaseEntity entity) {
		setJdbcUrlByEntity(entity);
		ProductTypeEnum productTypeEnum = ProductTypeEnum.getByIndex(entity.getDatabaseType());
		IMetaDataByJdbcService service = new MetaDataByJdbcServiceImpl(productTypeEnum);
		SqlGenerationVo sqlGenerationVo = new SqlGenerationVo();
		sqlGenerationVo.setSqlCreate(service.getTableDDL(entity.getJdbcUrl(), entity.getUserName(), entity.getPassword(), entity.getDatabaseSchema(), tableName));
		List<ColumnDescriptionVo> columns = getColumnDescriptionVos(tableName, entity);
		//TODO 后续做一个模块维护
		String flinkConfig;
		if (ProductTypeEnum.MONGODB.equals(productTypeEnum)) {
			flinkConfig = String.format("   'connector' = 'mongodb',\n" +
					"   'uri' = 'mongodb://%s:%s@%s:%s?authSource=admin',\n" +
					"   'database' = '%s',\n" +
					"   'collection' = '%s'\n" +
					"-- MongoDB 的连接器适用于 flink1.16 及以上版本，如果想要支持 mongodb，profiles 选择 flink-1.16 打包", entity.getUserName(), entity.getPassword(), entity.getDatabaseIp(), entity.getDatabasePort(), entity.getDatabaseSchema(), tableName);
		} else {
			flinkConfig = String.format("   'connector' = 'jdbc',\n" +
					"   'url' = '%s',\n" +
					"   'table-name' = '%s',\n" +
					"   'username' = '%s',\n" +
					"   'password' = '%s'\n" +
					"-- jdbc 模式 flink1.14 目前只支持 MySQL，PostgreSQL，Derby，具体版本支持程度请参阅官方文档", entity.getJdbcUrl(), tableName, entity.getUserName(), entity.getPassword());
		}
		List<ColumnDescription> columnDescriptions = BeanUtil.copyListProperties(columns, ColumnDescription::new);
		sqlGenerationVo.setFlinkSqlCreate(service.getFlinkTableSql(columnDescriptions, entity.getDatabaseSchema(), tableName, tableRemarks, flinkConfig));
		sqlGenerationVo.setSqlSelect(service.getSqlSelect(columnDescriptions, entity.getDatabaseSchema(), tableName, tableRemarks));
		return sqlGenerationVo;
	}

	@Override
	public List<TreeNodeVo> listMiddleDbTree() {
		DataDatabaseEntity entity = new DataDatabaseEntity();
		DataProjectCacheBean project = getProject();
		entity.setDatabaseName(project.getDbName());
		entity.setDatabaseSchema(project.getDbSchema());
		entity.setJdbcUrl(project.getDbUrl());
		entity.setUserName(project.getDbUsername());
		entity.setPassword(project.getDbPassword());
		entity.setName(project.getName() + "<中台库>");
		List<TreeNodeVo> nodeList = new ArrayList<>(1);
		TreeNodeVo dbNode = new TreeNodeVo();
		nodeList.add(dbNode);
		dbNode.setLeaf(false);
		dbNode.setIfLeaf(YesOrNo.YES.getValue());
		dbNode.setName(entity.getDatabaseName());
		dbNode.setLabel(entity.getDatabaseName());
		dbNode.setDescription(entity.getName());
		dbNode.setAttributes(entity);
		dbNode.setType(MiddleTreeNodeType.DB.getValue());
		List<TreeNodeVo> layerList = new ArrayList<>(1);
		dbNode.setChildren(layerList);
		//获取该项目下的所有表
		IMetaDataByJdbcService metaDataService = new MetaDataByJdbcServiceImpl(ProductTypeEnum.getByIndex(project.getDbType()));
		List<TableDescription> tableList = metaDataService.queryTableList(entity.getJdbcUrl(), entity.getUserName(), entity.getPassword(), entity.getDatabaseSchema());
		//分层子菜单
		for (DataHouseLayer layer : DataHouseLayer.values()) {
			TreeNodeVo layerNode = new TreeNodeVo();
			layerNode.setIfLeaf(YesOrNo.YES.getValue());
			layerNode.setLeaf(false);
			layerNode.setName(layer.name());
			layerNode.setLabel(layer.name());
			layerNode.setDescription(layer.getName());
			layerNode.setType(MiddleTreeNodeType.LAYER.getValue());
			layerList.add(layerNode);
			List<TreeNodeVo> tableNodeList = tableList.stream().filter(
					table -> table.getTableName().startsWith(layer.getTablePrefix()) && !DataHouseLayer.OTHER.equals(layer)
							|| DataHouseLayer.OTHER.equals(layer)
							&& !table.getTableName().startsWith(DataHouseLayer.ODS.getTablePrefix())
							&& !table.getTableName().startsWith(DataHouseLayer.DIM.getTablePrefix())
							&& !table.getTableName().startsWith(DataHouseLayer.DWD.getTablePrefix())
							&& !table.getTableName().startsWith(DataHouseLayer.DWS.getTablePrefix())
							&& !table.getTableName().startsWith(DataHouseLayer.ADS.getTablePrefix())).map(table -> {
				TreeNodeVo nodeVo = new TreeNodeVo();
				nodeVo.setIfLeaf(YesOrNo.NO.getValue());
				nodeVo.setLeaf(true);
				nodeVo.setName(table.getTableName());
				nodeVo.setLabel(table.getTableName());
				nodeVo.setDescription(table.getRemarks());
				nodeVo.setType(MiddleTreeNodeType.TABLE.getValue());
				return nodeVo;
			}).collect(Collectors.toList());
			layerNode.setChildren(tableNodeList);
		}
		return nodeList;
	}

	@Override
	public List<ColumnDescriptionVo> middleDbClumnInfo(String tableName) {
		DataDatabaseEntity entity = buildMiddleEntity();
		return getColumnDescriptionVos(tableName, entity);
	}

	@Override
	public SqlGenerationVo getMiddleDbSqlGeneration(String tableName, String tableRemarks) {
		DataDatabaseEntity entity = buildMiddleEntity();
		return getSqlGenerationVo(tableName, tableRemarks, entity);
	}

	@Override
	public TableInfo getTableInfo(String tableName) {
		TableInfo tableInfo = new TableInfo();
		tableInfo.setTableName(tableName);
		DataProjectCacheBean project = getProject();
		ProductTypeEnum productTypeEnum = ProductTypeEnum.getByIndex(project.getDbType());
		IMetaDataByJdbcService service = new MetaDataByJdbcServiceImpl(productTypeEnum);
		List<ColumnDescription> columnDescriptions = service.queryTableColumnMeta(project.getDbUrl(), project.getDbUsername(), project.getDbPassword(), project.getDbSchema(), tableName);
		List<String> pks = service.queryTablePrimaryKeys(project.getDbUrl(), project.getDbUsername(), project.getDbPassword(), project.getDbSchema(), tableName);
		List<ColumnInfo> columnInfos = new ArrayList<>();
		for (ColumnDescription columnDescription : columnDescriptions) {
			ColumnInfo columnInfo = new ColumnInfo(columnDescription);
			if (pks.contains(columnInfo.getName())) {
				columnInfo.setPk(1);
			}
			columnInfos.add(columnInfo);
		}
		tableInfo.setColumns(columnInfos);
		return tableInfo;
	}

	@Override
	public TableInfo saveTableInfo(TableInfo tableInfo) {
		List<ColumnInfo> columns = tableInfo.getColumns();
		List<ColumnDescription> columnDescriptions = new ArrayList<>();
		List<String> columnPkDescriptions = new ArrayList<>();
		for (ColumnInfo columnInfo : columns) {
			ColumnDescription columnDescription = ColumnInfo.makeColumnDescription(columnInfo);
			columnDescriptions.add(columnDescription);
			if (columnDescription.isPk()) {
				columnPkDescriptions.add(columnDescription.getFieldName());
			}
		}
		DataProjectCacheBean project = getProject();
		ProductTypeEnum productTypeEnum = ProductTypeEnum.getByIndex(project.getDbType());
		List<String> ddlCreateTableSQL = GenerateSqlUtils.getDDLCreateTableSQL(productTypeEnum, columnDescriptions, columnPkDescriptions, project.getDbSchema(), tableInfo.getTableName(), tableInfo.getTableCn(), false, null);
		IMetaDataByJdbcService metaDataByJdbcService = new MetaDataByJdbcServiceImpl(productTypeEnum);
		for (String sql : ddlCreateTableSQL) {
			metaDataByJdbcService.executeSql(project.getDbUrl(), project.getDbUsername(), project.getDbPassword(), sql);
		}
		return tableInfo;
	}

	@Override
	public void deleteTableInfo(String tableName) {
		DataProjectCacheBean project = getProject();
		ProductTypeEnum productTypeEnum = ProductTypeEnum.getByIndex(project.getDbType());
		TargetDataSourceProperties properties = new TargetDataSourceProperties();
		properties.setUrl(project.getDbUrl());
		properties.setDriverClassName(productTypeEnum.getDriveClassName());
		properties.setTargetProductType(productTypeEnum);
		properties.setUsername(project.getDbUsername());
		properties.setPassword(project.getDbPassword());
		//清除表
		try (HikariDataSource targetDataSource = DataSourceUtils.createTargetDataSource(properties)) {
			DatabaseOperatorFactory.createDatabaseOperator(targetDataSource, productTypeEnum)
					.dropTable(project.getDbSchema(), tableName);
		}
	}

	/**
	 * 构建中间库的entity
	 *
	 * @return
	 */
	private DataDatabaseEntity buildMiddleEntity() {
		DataDatabaseEntity entity = new DataDatabaseEntity();
		DataProjectCacheBean project = getProject();
		entity.setDatabaseType(project.getDbType());
		entity.setDatabaseName(project.getDbName());
		entity.setDatabaseSchema(project.getDbSchema());
		entity.setJdbcUrl(project.getDbUrl());
		entity.setUserName(project.getDbUsername());
		entity.setPassword(project.getDbPassword());
		return entity;
	}

}
