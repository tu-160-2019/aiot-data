package net.srt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.AllArgsConstructor;
import net.srt.api.module.data.integrate.constant.TaskType;
import net.srt.api.module.data.integrate.dto.PreviewNameMapperDto;
import net.srt.api.module.quartz.QuartzDataAccessApi;
import net.srt.constants.AccessMode;
import net.srt.constants.DataHouseLayer;
import net.srt.constants.SuperAdminEnum;
import net.srt.constants.YesOrNo;
import net.srt.convert.DataAccessConvert;
import net.srt.convert.DataAccessTaskConvert;
import net.srt.dao.DataAccessDao;
import net.srt.dao.DataAccessIncreaseLogDao;
import net.srt.dao.DataDatabaseDao;
import net.srt.dto.DataAccessClientDto;
import net.srt.dto.PreviewMapDto;
import net.srt.entity.DataAccessEntity;
import net.srt.entity.DataAccessIncreaseLogEntity;
import net.srt.entity.DataDatabaseEntity;
import net.srt.framework.common.cache.bean.DataProjectCacheBean;
import net.srt.framework.common.config.Config;
import net.srt.framework.common.exception.ServerException;
import net.srt.framework.common.page.PageResult;
import net.srt.framework.common.utils.BeanUtil;
import net.srt.framework.mybatis.service.impl.BaseServiceImpl;
import net.srt.framework.security.user.SecurityUser;
import net.srt.framework.security.user.UserDetail;
import net.srt.query.DataAccessQuery;
import net.srt.query.DataAccessTaskDetailQuery;
import net.srt.query.DataAccessTaskQuery;
import net.srt.service.DataAccessService;
import net.srt.service.DataAccessTaskDetailService;
import net.srt.service.DataAccessTaskService;
import net.srt.vo.DataAccessTaskDetailVO;
import net.srt.vo.DataAccessTaskVO;
import net.srt.vo.DataAccessVO;
import net.srt.vo.PreviewNameMapperVo;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import srt.cloud.framework.dbswitch.common.entity.PatternMapper;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.type.SourceType;
import srt.cloud.framework.dbswitch.common.util.DbswitchStrUtils;
import srt.cloud.framework.dbswitch.common.util.PatterNameUtils;
import srt.cloud.framework.dbswitch.common.util.StringUtil;
import srt.cloud.framework.dbswitch.core.model.ColumnDescription;
import srt.cloud.framework.dbswitch.core.model.TableDescription;
import srt.cloud.framework.dbswitch.core.service.IMetaDataByJdbcService;
import srt.cloud.framework.dbswitch.core.service.impl.MetaDataByJdbcServiceImpl;
import srt.cloud.framework.dbswitch.data.config.DbswichProperties;
import srt.cloud.framework.dbswitch.data.entity.SourceDataSourceProperties;
import srt.cloud.framework.dbswitch.data.entity.TargetDataSourceProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据集成-数据接入
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-10-24
 */
@Service
@AllArgsConstructor
public class DataAccessServiceImpl extends BaseServiceImpl<DataAccessDao, DataAccessEntity> implements DataAccessService {

	private final DataDatabaseDao dataDatabaseDao;
	private final QuartzDataAccessApi quartzDataAccessApi;
	private final DataAccessTaskService dataAccessTaskService;
	private final DataAccessTaskDetailService dataAccessTaskDetailService;
	private final DataAccessIncreaseLogDao increaseLogDao;
	private final Config config;

	private final static String STRING_EMPTY = "<!空>";
	private final static String STRING_DELETE = "<!删除>";

	@Override
	public PageResult<DataAccessVO> page(DataAccessQuery query) {
		IPage<DataAccessEntity> page = baseMapper.selectPage(getPage(query), getWrapper(query));

		return new PageResult<>(DataAccessConvert.INSTANCE.convertList(page.getRecords()), page.getTotal());
	}

	@Override
	public DataAccessClientDto getById(Long id) {
		DataAccessEntity dataAccessEntity = baseMapper.selectById(id);
		if (dataAccessEntity == null) {
			return null;
		}
		UserDetail user = SecurityUser.getUser();
		if (!SuperAdminEnum.YES.getValue().equals(user.getSuperAdmin()) && !CollectionUtils.isEmpty(user.getDataScopeList()) && !user.getDataScopeList().contains(dataAccessEntity.getOrgId())) {
			throw new ServerException("对不起，您无权查看该数据接入信息！");
		}
		DbswichProperties dataAccessJson = dataAccessEntity.getDataAccessJson();
		SourceDataSourceProperties source = dataAccessJson.getSource().get(0);
		TargetDataSourceProperties target = dataAccessJson.getTarget();
		return DataAccessClientDto.builder().id(dataAccessEntity.getId()).orgId(dataAccessEntity.getOrgId()).accessMode(dataAccessEntity.getAccessMode()).taskName(dataAccessEntity.getTaskName())
				.cron(dataAccessEntity.getCron()).description(dataAccessEntity.getDescription()).projectId(dataAccessEntity.getProjectId())
				.sourceDatabaseId(dataAccessEntity.getSourceDatabaseId()).targetDatabaseId(dataAccessEntity.getTargetDatabaseId())
				.sourceType(source.getSourceType()).sourceSql(source.getSourceSql()).targetTable(target.getTargetTable()).sourcePrimaryKeys(source.getSourcePrimaryKeys())
				.taskType(dataAccessEntity.getTaskType()).batchSize(source.getFetchSize()).tableNameMapper(source.getRegexTableMapper())
				.columnNameMapper(source.getRegexColumnMapper()).includeOrExclude(source.getIncludeOrExclude())
				.sourceSelectedTables(YesOrNo.YES.getValue().equals(source.getIncludeOrExclude()) ? DbswitchStrUtils.stringToList(source.getSourceIncludes()) : DbswitchStrUtils.stringToList(source.getSourceExcludes()))
				.targetAutoIncrement(target.getCreateTableAutoIncrement()).targetDataSync(target.getChangeDataSync()).targetDropTable(target.getTargetDrop())
				.targetIndexCreate(target.getIndexCreate()).targetLowerCase(target.getLowercase()).targetOnlyCreate(target.getOnlyCreate())
				.targetSyncExit(target.getSyncExist()).targetUpperCase(target.getUppercase())
				.changeDataSyncType(StringUtil.isNotBlank(target.getChangeDataSyncType()) ? target.getChangeDataSyncType() : "").mapperType(source.getMapperType())
				.configMap(source.getConfigMap()).increaseColumnName(source.getIncreaseColumnName()).build();
	}

	private LambdaQueryWrapper<DataAccessEntity> getWrapper(DataAccessQuery query) {
		LambdaQueryWrapper<DataAccessEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.like(StringUtil.isNotBlank(query.getTaskName()), DataAccessEntity::getTaskName, query.getTaskName());
		wrapper.eq(query.getProjectId() != null, DataAccessEntity::getProjectId, query.getProjectId());
		wrapper.eq(query.getDataDatabaseId() != null, DataAccessEntity::getSourceDatabaseId, query.getDataDatabaseId());
		wrapper.eq(query.getStatus() != null, DataAccessEntity::getStatus, query.getStatus());
		wrapper.eq(query.getRunStatus() != null, DataAccessEntity::getRunStatus, query.getRunStatus());
		dataScopeWithOrgId(wrapper);
		wrapper.orderByDesc(DataAccessEntity::getCreateTime);
		wrapper.orderByDesc(DataAccessEntity::getId);
		return wrapper;
	}

	@Override
	public void save(DataAccessClientDto dto) {
		dto.setProjectId(getProjectId());
		DataAccessEntity dataAccessEntity = buildDataAccessEntity(dto);
		dataAccessEntity.setOrgId(dto.getOrgId());
		dataAccessEntity.setProjectId(dto.getProjectId());
		baseMapper.insert(dataAccessEntity);

	}

	@Override
	public void update(DataAccessClientDto dto) {
		dto.setProjectId(getProjectId());
		DataAccessEntity entity = buildDataAccessEntity(dto);
		entity.setOrgId(dto.getOrgId());
		entity.setProjectId(dto.getProjectId());
		updateById(entity);
	}

	private DataAccessEntity buildDataAccessEntity(DataAccessClientDto dto) {
		if (TaskType.ONE_TIME_FULL_PERIODIC_INCR_SYNC.getCode().equals(dto.getTaskType()) && !CronExpression.isValidExpression(dto.getCron())) {
			throw new ServerException("cron表达式有误，请检查！");
		}
		DbswichProperties dbswichProperties = new DbswichProperties();
		List<SourceDataSourceProperties> source = new ArrayList<>(1);
		SourceDataSourceProperties sourceDataSourceProperties = new SourceDataSourceProperties();
		sourceDataSourceProperties.setSourceType(dto.getSourceType());
		sourceDataSourceProperties.setSourceSql(SourceType.SQL.getCode().equals(dto.getSourceType()) ? dto.getSourceSql() : null);
		sourceDataSourceProperties.setSourcePrimaryKeys(dto.getSourcePrimaryKeys());
		DataDatabaseEntity sourceDatabase = dataDatabaseDao.selectById(dto.getSourceDatabaseId());
		//构建源端
		ProductTypeEnum sourceProductType = ProductTypeEnum.getByIndex(sourceDatabase.getDatabaseType());
		sourceDataSourceProperties.setSourceProductType(sourceProductType);
		sourceDataSourceProperties.setUrl(StringUtil.isBlank(sourceDatabase.getJdbcUrl()) ? sourceProductType.getUrl()
				.replace("{host}", sourceDatabase.getDatabaseIp())
				.replace("{port}", sourceDatabase.getDatabasePort())
				.replace("{database}", sourceDatabase.getDatabaseName()) : sourceDatabase.getJdbcUrl());
		sourceDataSourceProperties.setDriverClassName(sourceProductType.getDriveClassName());
		sourceDataSourceProperties.setUsername(sourceDatabase.getUserName());
		sourceDataSourceProperties.setPassword(sourceDatabase.getPassword());
		sourceDataSourceProperties.setFetchSize(dto.getBatchSize());
		sourceDataSourceProperties.setSourceSchema(sourceDatabase.getDatabaseSchema());
		Integer includeOrExclude = dto.getIncludeOrExclude();
		sourceDataSourceProperties.setIncludeOrExclude(includeOrExclude);
		//如果是包含表
		if (YesOrNo.YES.getValue().equals(includeOrExclude)) {
			sourceDataSourceProperties.setSourceIncludes(StringUtils.join(dto.getSourceSelectedTables(), ","));
		} else {
			sourceDataSourceProperties.setSourceExcludes(StringUtils.join(dto.getSourceSelectedTables(), ","));
		}
		sourceDataSourceProperties.setRegexTableMapper(dto.getTableNameMapper());
		List<PatternMapper> columnNameMapper = dto.getColumnNameMapper();
		if (SourceType.SQL.getCode().equals(dto.getSourceType())) {
			if (columnNameMapper.stream().anyMatch(item -> StringUtil.isBlank(item.getToValue()))) {
				throw new ServerException("自定义 SQL 接入不允许映射字段为空！");
			}
		}
		sourceDataSourceProperties.setRegexColumnMapper(columnNameMapper);
		sourceDataSourceProperties.setMapperType(dto.getMapperType());
		sourceDataSourceProperties.setIncreaseColumnName(dto.getIncreaseColumnName());
		sourceDataSourceProperties.setConfigMap(dto.getConfigMap());
		source.add(sourceDataSourceProperties);
		//构建目标端
		TargetDataSourceProperties target = new TargetDataSourceProperties();
		if (AccessMode.ODS.getValue().equals(dto.getAccessMode())) {
			DataProjectCacheBean project = getProject(dto.getProjectId());
			ProductTypeEnum productTypeEnum = ProductTypeEnum.getByIndex(project.getDbType());
			target.setTargetProductType(productTypeEnum);
			target.setDriverClassName(productTypeEnum.getDriveClassName());
			target.setUrl(project.getDbUrl());
			target.setUsername(project.getDbUsername());
			target.setPassword(project.getDbPassword());
			target.setTargetSchema(project.getDbSchema());
			target.setTablePrefix(DataHouseLayer.ODS.getTablePrefix());
		} else {
			DataDatabaseEntity targetDatabase = dataDatabaseDao.selectById(dto.getTargetDatabaseId());
			ProductTypeEnum targetProductType = ProductTypeEnum.getByIndex(targetDatabase.getDatabaseType());
			target.setTargetProductType(targetProductType);
			target.setUrl(StringUtil.isBlank(targetDatabase.getJdbcUrl()) ? targetProductType.getUrl()
					.replace("{host}", targetDatabase.getDatabaseIp())
					.replace("{port}", targetDatabase.getDatabasePort())
					.replace("{database}", targetDatabase.getDatabaseName()) : targetDatabase.getJdbcUrl());
			target.setDriverClassName(targetProductType.getDriveClassName());
			target.setUsername(targetDatabase.getUserName());
			target.setPassword(targetDatabase.getPassword());
			target.setTargetSchema(targetDatabase.getDatabaseSchema());
		}
		target.setTargetDrop(dto.isTargetDropTable());
		target.setSyncExist(dto.isTargetSyncExit());
		target.setOnlyCreate(dto.isTargetOnlyCreate());
		target.setIndexCreate(dto.isTargetIndexCreate());
		target.setLowercase(dto.isTargetLowerCase());
		target.setUppercase(dto.isTargetUpperCase());
		target.setCreateTableAutoIncrement(dto.isTargetAutoIncrement());
		target.setChangeDataSync(dto.isTargetDataSync());
		target.setChangeDataSyncType(dto.getChangeDataSyncType());
		target.setTargetTable(SourceType.SQL.getCode().equals(dto.getSourceType()) ? dto.getTargetTable() : null);
		dbswichProperties.setSource(source);
		dbswichProperties.setTarget(target);

		return DataAccessEntity.builder().id(dto.getId()).taskName(dto.getTaskName()).sourceType(dto.getSourceType()).taskType(dto.getTaskType()).description(dto.getDescription())
				.accessMode(dto.getAccessMode()).cron(dto.getCron()).projectId(dto.getProjectId()).status(YesOrNo.NO.getValue())
				.targetDatabaseId(AccessMode.CUSTOM.getValue().equals(dto.getAccessMode()) ? dto.getTargetDatabaseId() : null).sourceDatabaseId(dto.getSourceDatabaseId())/*.runStatus(CommonRunStatus.WAITING.getCode())*/
				.dataAccessJson(dbswichProperties).build();
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(List<Long> idList) {
		removeByIds(idList);
		for (Long id : idList) {
			quartzDataAccessApi.cancleAccess(id);
			//删除记录
			dataAccessTaskService.deleteByAccessId(id);
			dataAccessTaskDetailService.deleteByAccessId(id);
			//删除增量日志
			LambdaQueryWrapper<DataAccessIncreaseLogEntity> wrapper = Wrappers.lambdaQuery();
			wrapper.eq(DataAccessIncreaseLogEntity::getDataAccessId, id);
			increaseLogDao.delete(wrapper);
		}
	}

	@Override
	public DataAccessEntity loadById(Long id) {
		return baseMapper.selectById(id);
	}

	@Override
	public void updateStartInfo(Long dataAccessId) {
		baseMapper.updateStartInfo(dataAccessId);
	}

	@Override
	public void updateEndInfo(Long dataAccessId, Integer runStatus, Date nextRunTime) {
		baseMapper.updateEndInfo(dataAccessId, runStatus, nextRunTime);
	}

	@Override
	public List<PreviewNameMapperDto> getTableMap(Long id) {
		PreviewMapDto previewMapDto = getPreviewMapDto(id);
		return BeanUtil.copyListProperties(previewTableMap(previewMapDto).stream().filter(item -> StringUtil.isNotBlank(item.getTargetName())).collect(Collectors.toList()), PreviewNameMapperDto::new);
	}


	@Override
	public List<PreviewNameMapperDto> getColumnMap(Long id, String tableName) {
		PreviewMapDto previewMapDto = getPreviewMapDto(id);
		previewMapDto.setPreiveTableName(tableName);
		return BeanUtil.copyListProperties(previewColumnMap(previewMapDto).stream().filter(item -> !STRING_DELETE.equals(item.getTargetName())).collect(Collectors.toList()), PreviewNameMapperDto::new);
	}

	private PreviewMapDto getPreviewMapDto(Long id) {
		DataAccessEntity dataAccessEntity = baseMapper.selectById(id);
		DbswichProperties dbswichProperties = dataAccessEntity.getDataAccessJson();
		PreviewMapDto previewMapDto = new PreviewMapDto();
		previewMapDto.setSourceDatabaseId(dataAccessEntity.getSourceDatabaseId());
		TargetDataSourceProperties targetDataSourceProperties = dbswichProperties.getTarget();
		SourceDataSourceProperties sourceDataSourceProperties = dbswichProperties.getSource().get(0);
		previewMapDto.setIncludeOrExclude(sourceDataSourceProperties.getIncludeOrExclude());
		List<String> sourceSelectedTables = new ArrayList<>();
		if (StringUtil.isNotBlank(sourceDataSourceProperties.getSourceIncludes())) {
			sourceSelectedTables.addAll(Arrays.asList(sourceDataSourceProperties.getSourceIncludes().split(",")));
		}
		if (StringUtil.isNotBlank(sourceDataSourceProperties.getSourceExcludes())) {
			sourceSelectedTables.addAll(Arrays.asList(sourceDataSourceProperties.getSourceExcludes().split(",")));
		}
		previewMapDto.setSourceSelectedTables(sourceSelectedTables);
		previewMapDto.setTableNameMapper(sourceDataSourceProperties.getRegexTableMapper());
		previewMapDto.setColumnNameMapper(sourceDataSourceProperties.getRegexColumnMapper());
		previewMapDto.setTablePrefix(targetDataSourceProperties.getTablePrefix());
		previewMapDto.setTargetLowerCase(targetDataSourceProperties.getLowercase());
		previewMapDto.setTargetUpperCase(targetDataSourceProperties.getUppercase());
		return previewMapDto;
	}

	@Override
	public List<PreviewNameMapperVo> previewTableMap(PreviewMapDto previewMapDto) {
		boolean include = YesOrNo.YES.getValue().equals(previewMapDto.getIncludeOrExclude());
		List<PreviewNameMapperVo> result = new ArrayList<>(10);
		List<TableDescription> allTableNames = getAllTableNames(previewMapDto);
		//如果选择的表名为空，则预览全部
		if (CollectionUtils.isEmpty(previewMapDto.getSourceSelectedTables())) {
			for (TableDescription td : allTableNames) {
				String targetName = PatterNameUtils.getFinalName(
						td.getTableName(), previewMapDto.getTableNameMapper());
				if (previewMapDto.isTargetLowerCase()) {
					targetName = targetName.toLowerCase();
				} else if (previewMapDto.isTargetUpperCase()) {
					targetName = targetName.toUpperCase();
				}
				if (StringUtils.isNotBlank(previewMapDto.getTablePrefix()) && !targetName.startsWith(previewMapDto.getTablePrefix())) {
					targetName = previewMapDto.getTablePrefix() + targetName;
				}
				result.add(PreviewNameMapperVo.builder()
						.originalName(td.getTableName())
						.targetName(StringUtils.isNotBlank(targetName) ? targetName : STRING_EMPTY)
						.remarks(StringUtil.isNotBlank(td.getRemarks()) ? td.getRemarks() : td.getTableName())
						.build());
			}
		} else {
			if (include) {
				for (String name : previewMapDto.getSourceSelectedTables()) {
					if (StringUtils.isNotBlank(name)) {
						String targetName = PatterNameUtils.getFinalName(
								name, previewMapDto.getTableNameMapper());
						if (previewMapDto.isTargetLowerCase()) {
							targetName = targetName.toLowerCase();
						} else if (previewMapDto.isTargetUpperCase()) {
							targetName = targetName.toUpperCase();
						}
						if (StringUtils.isNotBlank(previewMapDto.getTablePrefix()) && !targetName.startsWith(previewMapDto.getTablePrefix())) {
							targetName = previewMapDto.getTablePrefix() + targetName;
						}
						TableDescription tableDescription = allTableNames.stream().filter(item -> name.equals(item.getTableName())).findFirst().orElse(null);
						result.add(PreviewNameMapperVo.builder()
								.originalName(name)
								.targetName(StringUtils.isNotBlank(targetName) ? targetName : STRING_EMPTY)
								.remarks(tableDescription != null ? tableDescription.getRemarks() : null)
								.build());
					}
				}
			} else {
				for (TableDescription td : allTableNames) {
					if (!previewMapDto.getSourceSelectedTables().contains(td.getTableName())) {
						String targetName = PatterNameUtils.getFinalName(td.getTableName(), previewMapDto.getTableNameMapper());
						if (previewMapDto.isTargetLowerCase()) {
							targetName = targetName.toLowerCase();
						} else if (previewMapDto.isTargetUpperCase()) {
							targetName = targetName.toUpperCase();
						}
						if (StringUtils.isNotBlank(previewMapDto.getTablePrefix()) && !targetName.startsWith(previewMapDto.getTablePrefix())) {
							targetName = previewMapDto.getTablePrefix() + targetName;
						}
						result.add(PreviewNameMapperVo.builder()
								.originalName(td.getTableName())
								.targetName(StringUtils.isNotBlank(targetName) ? targetName : STRING_EMPTY)
								.remarks(td.getRemarks())
								.build());
					}
				}
			}
		}
		return result;
	}


	@Override
	public List<PreviewNameMapperVo> previewColumnMap(PreviewMapDto previewMapDto) {
		if (SourceType.SQL.getCode().equals(previewMapDto.getSourceType())) {
			if (previewMapDto.getSourceDatabaseId() == null || StringUtils.isBlank(previewMapDto.getSourceSql())) {
				throw new ServerException("请选择源端数据库，填写源端 SQL 语句（单条 SELECT）");
			}
		} else {
			if (previewMapDto.getSourceDatabaseId() == null || StringUtils.isBlank(previewMapDto.getPreiveTableName())) {
				throw new ServerException("请选择源端数据库，数据表");
			}
		}

		List<PreviewNameMapperVo> result = new ArrayList<>(10);
		DataDatabaseEntity databaseEntity;
		if (previewMapDto.getSourceDatabaseId() == -1) {
			databaseEntity = buildDatabase();
		} else {
			databaseEntity = dataDatabaseDao.selectById(previewMapDto.getSourceDatabaseId());
		}
		if (databaseEntity == null) {
			throw new ServerException("选择的源端数据库已不存在！");
		}
		ProductTypeEnum productTypeEnum = ProductTypeEnum.getByIndex(databaseEntity.getDatabaseType());
		IMetaDataByJdbcService service = new MetaDataByJdbcServiceImpl(productTypeEnum);
		List<ColumnDescription> columns;
		if (SourceType.SQL.getCode().equals(previewMapDto.getSourceType())) {
			columns = service.querySqlColumnMeta(StringUtil.isBlank(databaseEntity.getJdbcUrl()) ? productTypeEnum.getUrl()
					.replace("{host}", databaseEntity.getDatabaseIp())
					.replace("{port}", databaseEntity.getDatabasePort())
					.replace("{database}", databaseEntity.getDatabaseName()) : databaseEntity.getJdbcUrl(), databaseEntity.getUserName(), databaseEntity.getPassword(), previewMapDto.getSourceSql());
		} else {
			columns = service.queryTableColumnMetaOnly(StringUtil.isBlank(databaseEntity.getJdbcUrl()) ? productTypeEnum.getUrl()
							.replace("{host}", databaseEntity.getDatabaseIp())
							.replace("{port}", databaseEntity.getDatabasePort())
							.replace("{database}", databaseEntity.getDatabaseName()) : databaseEntity.getJdbcUrl(), databaseEntity.getUserName(), databaseEntity.getPassword(), databaseEntity.getDatabaseSchema(),
					previewMapDto.getPreiveTableName());
		}
		for (ColumnDescription cd : columns) {
			String targetName = PatterNameUtils.getFinalName(cd.getFieldName(), previewMapDto.getColumnNameMapper());
			if (SourceType.SQL.getCode().equals(previewMapDto.getSourceType()) && StringUtil.isBlank(targetName)) {
				throw new ServerException("自定义 SQL 接入不允许映射字段为空！");
			}
			if (previewMapDto.isTargetLowerCase()) {
				targetName = targetName.toLowerCase();
			} else if (previewMapDto.isTargetUpperCase()) {
				targetName = targetName.toUpperCase();
			}
			result.add(PreviewNameMapperVo.builder()
					.originalName(cd.getFieldName())
					.targetName(StringUtils.isNotBlank(targetName) ? targetName : STRING_DELETE)
					.remarks(StringUtil.isNotBlank(cd.getRemarks()) ? cd.getRemarks() : cd.getFieldName())
					.build());
		}
		return result;
	}

	private DataDatabaseEntity buildDatabase() {
		DataDatabaseEntity databaseEntity;
		DataProjectCacheBean project = getProject();
		databaseEntity = new DataDatabaseEntity();
		databaseEntity.setJdbcUrl(project.getDbUrl());
		databaseEntity.setDatabaseName(project.getDbName());
		databaseEntity.setDatabaseSchema(project.getDbSchema());
		databaseEntity.setUserName(project.getDbUsername());
		databaseEntity.setPassword(project.getDbPassword());
		databaseEntity.setDatabaseType(project.getDbType());
		return databaseEntity;
	}

	@Override
	public void release(Long id) {
		DataAccessEntity dataAccessEntity = baseMapper.selectById(id);
		if (TaskType.REAL_TIME_SYNC.getCode().equals(dataAccessEntity.getTaskType())) {
			throw new ServerException("暂不支持实时同步！");
		}
		quartzDataAccessApi.releaseAccess(id);
		//更新状态，发布时间和发布人
		baseMapper.changeStatus(id, YesOrNo.YES.getValue(), new Date(), SecurityUser.getUserId());
	}

	@Override
	public void cancle(Long id) {
		quartzDataAccessApi.cancleAccess(id);
		//更新状态
		baseMapper.changeStatus(id, YesOrNo.NO.getValue(), null, null);
	}

	@Override
	public void handRun(Long id) {
		quartzDataAccessApi.handRun(id);
	}

	@Override
	public void stopHandTask(String executeNo) {
		quartzDataAccessApi.stopHandTask(executeNo);
	}

	@Override
	public PageResult<DataAccessTaskVO> taskPage(DataAccessTaskQuery taskQuery) {
		return dataAccessTaskService.page(taskQuery);
	}

	@Override
	public void deleteTask(List<Long> idList) {
		dataAccessTaskService.delete(idList);
		//删除对应的同步结果
		dataAccessTaskDetailService.deleteByTaskId(idList);
	}

	@Override
	public PageResult<DataAccessTaskDetailVO> taskDetailPage(DataAccessTaskDetailQuery detailQuery) {
		detailQuery.setProjectId(getProjectId());
		return dataAccessTaskDetailService.page(detailQuery);
	}

	@Override
	public DataAccessTaskVO getTaskById(Long id) {
		return DataAccessTaskConvert.INSTANCE.convert(dataAccessTaskService.getById(id));
	}


	private List<TableDescription> getAllTableNames(PreviewMapDto previewMapDto) {
		DataDatabaseEntity databaseEntity;
		if (previewMapDto.getSourceDatabaseId() == null) {
			throw new ServerException("请选择源端数据库");
		} else {
			if (previewMapDto.getSourceDatabaseId() == -1) {
				databaseEntity = buildDatabase();
			} else {
				databaseEntity = dataDatabaseDao.selectById(previewMapDto.getSourceDatabaseId());
			}
		}

		if (databaseEntity == null) {
			throw new ServerException("选择的源端数据库已不存在！");
		}

		ProductTypeEnum productTypeEnum = ProductTypeEnum.getByIndex(databaseEntity.getDatabaseType());
		IMetaDataByJdbcService service = new MetaDataByJdbcServiceImpl(productTypeEnum);

		return service.queryTableList(StringUtil.isBlank(databaseEntity.getJdbcUrl()) ? productTypeEnum.getUrl()
						.replace("{host}", databaseEntity.getDatabaseIp())
						.replace("{port}", databaseEntity.getDatabasePort())
						.replace("{database}", databaseEntity.getDatabaseName()) : databaseEntity.getJdbcUrl(), databaseEntity.getUserName(), databaseEntity.getPassword(),
				databaseEntity.getDatabaseSchema()).stream().filter(td -> !td.isViewTable())
				.collect(Collectors.toList());
	}
}
