// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.data.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import net.srt.flink.common.utils.LogUtil;
import srt.cloud.framework.dbswitch.common.type.DBTableType;
import srt.cloud.framework.dbswitch.common.type.SourceType;
import srt.cloud.framework.dbswitch.common.util.DbswitchStrUtils;
import srt.cloud.framework.dbswitch.common.util.PatterNameUtils;
import srt.cloud.framework.dbswitch.core.model.TableDescription;
import srt.cloud.framework.dbswitch.core.service.IMetaDataByDatasourceService;
import srt.cloud.framework.dbswitch.core.service.impl.MetaDataByDataSourceServiceImpl;
import srt.cloud.framework.dbswitch.data.config.DbswichProperties;
import srt.cloud.framework.dbswitch.data.domain.DbSwitchResult;
import srt.cloud.framework.dbswitch.data.domain.DbSwitchTableResult;
import srt.cloud.framework.dbswitch.data.domain.PerfStat;
import srt.cloud.framework.dbswitch.data.entity.SourceDataSourceProperties;
import srt.cloud.framework.dbswitch.data.entity.TargetDataSourceProperties;
import srt.cloud.framework.dbswitch.data.handler.MigrationHandler;
import srt.cloud.framework.dbswitch.data.handler.TableResultHandler;
import srt.cloud.framework.dbswitch.data.util.DataSourceUtils;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * 数据迁移主逻辑类
 *
 * @author jrl
 */
@Slf4j
public class MigrationService {

	/**
	 * JSON序列化工具
	 */
	private final ObjectMapper jackson = new ObjectMapper();

	/**
	 * 性能统计记录表
	 */
	private final List<PerfStat> perfStats = new ArrayList<>();

	/**
	 * 任务列表
	 */
	private List<MigrationHandler> migrationHandlers = new ArrayList<>();

	/**
	 * 线程是否被中断的标识
	 */
	private volatile boolean interrupted = false;

	/**
	 * 配置参数
	 */
	private final DbswichProperties properties;

	/**
	 * 配置参数
	 */
	private final TableResultHandler tableResultHandler;


	/**
	 * 构造函数
	 *
	 * @param properties 配置信息
	 */
	public MigrationService(DbswichProperties properties, TableResultHandler tableResultHandler) {
		this.properties = Objects.requireNonNull(properties, "properties is null");
		this.tableResultHandler = tableResultHandler;
	}

	/**
	 * 中断执行中的任务
	 */
	synchronized public void interrupt() {
		this.interrupted = true;
		migrationHandlers.forEach(MigrationHandler::interrupt);
	}

	/**
	 * 执行主逻辑
	 */
	public DbSwitchResult run() throws Exception {
		StopWatch watch = new StopWatch();
		watch.start();

		DbSwitchResult dbSwitchResult = new DbSwitchResult();

		log.info("dbswitch data service is started....");

		TargetDataSourceProperties target = properties.getTarget();
		try (HikariDataSource targetDataSource = DataSourceUtils
				.createTargetDataSource(target)) {
			int sourcePropertiesIndex = 0;
			int totalTableCount = 0;
			List<SourceDataSourceProperties> sourcesProperties = properties.getSource();
			for (SourceDataSourceProperties sourceProperties : sourcesProperties) {
				if (interrupted) {
					throw new RuntimeException("task is interrupted");
				}
				try (HikariDataSource sourceDataSource = DataSourceUtils
						.createSourceDataSource(sourceProperties)) {
					IMetaDataByDatasourceService
							sourceMetaDataService = new MetaDataByDataSourceServiceImpl(sourceDataSource, sourceProperties.getSourceProductType());

					List<CompletableFuture<Void>> futures = new ArrayList<>();
					AtomicInteger numberOfFailures = new AtomicInteger(0);
					AtomicLong currentSourceRowCount = new AtomicLong(0L);

					if (SourceType.TABLE.getCode().equals(sourceProperties.getSourceType())) {
						// 判断处理的策略：是排除还是包含
						List<String> includes = DbswitchStrUtils
								.stringToList(sourceProperties.getSourceIncludes());
						log.info("Includes tables is :{}", jackson.writeValueAsString(includes));
						List<String> filters = DbswitchStrUtils
								.stringToList(sourceProperties.getSourceExcludes());
						log.info("Filter tables is :{}", jackson.writeValueAsString(filters));

						boolean useExcludeTables = includes.isEmpty();
						if (useExcludeTables) {
							log.info("!!!! Use dbswitch.source[{}].source-excludes parameter to filter tables",
									sourcePropertiesIndex);
						} else {
							log.info("!!!! Use dbswitch.source[{}].source-includes parameter to filter tables",
									sourcePropertiesIndex);
						}

						List<String> schemas = DbswitchStrUtils.stringToList(sourceProperties.getSourceSchema());
						log.info("Source schema names is :{}", jackson.writeValueAsString(schemas));

						for (String schema : schemas) {
							if (interrupted) {
								break;
							}
							List<TableDescription> tableList = sourceMetaDataService.queryTableList(schema);
							if (tableList.isEmpty()) {
								log.warn("### Find source database table list empty for schema name is : {}", schema);
							} else {
								DBTableType tableType = sourceProperties.getTableType();
								for (TableDescription td : tableList) {
									// 当没有配置迁移的表名时，默认为根据类型同步所有
									if (includes.isEmpty()) {
										if (null != tableType && !tableType.name().equals(td.getTableType())) {
											continue;
										}
									}

									String tableName = td.getTableName();

									if (useExcludeTables) {
										if (!filters.contains(tableName)) {
											futures.add(makeFutureTask(td, sourcePropertiesIndex, sourceDataSource, targetDataSource, numberOfFailures, currentSourceRowCount, dbSwitchResult));
										}
									} else {
										if (includes.size() == 1 && (includes.get(0).contains("*") || includes.get(0)
												.contains("?"))) {
											if (Pattern.matches(includes.get(0), tableName)) {
												futures.add(makeFutureTask(td, sourcePropertiesIndex, sourceDataSource, targetDataSource, numberOfFailures, currentSourceRowCount, dbSwitchResult));
											}
										} else if (includes.contains(tableName)) {
											futures.add(makeFutureTask(td, sourcePropertiesIndex, sourceDataSource, targetDataSource, numberOfFailures, currentSourceRowCount, dbSwitchResult));
										}
									}
								}
							}
						}
					} else if (SourceType.SQL.getCode().equals(sourceProperties.getSourceType())) {
						TableDescription td = new TableDescription();
						td.setTableName(sourceProperties.getSourceSql());
						td.setTableType(DBTableType.SQL.name());
						td.setSchemaName(sourceProperties.getSourceSchema());
						td.setSql(sourceProperties.getSourceSql());
						log.info("Source sql is :{}", sourceProperties.getSourceSql());
						futures.add(makeFutureTask(td, sourcePropertiesIndex, sourceDataSource, targetDataSource, numberOfFailures, currentSourceRowCount, dbSwitchResult));
					}

					if (!interrupted) {
						CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).get();
						log.info(
								"#### Complete data migration for the [ {} ] data source:\ntotal table count={}\nfailure count={}\ntotal count size={}",
								sourcePropertiesIndex, futures.size(), numberOfFailures.get(),
								currentSourceRowCount.get());
						perfStats.add(new PerfStat(sourcePropertiesIndex, futures.size(),
								numberOfFailures.get(), currentSourceRowCount.get()));
						++sourcePropertiesIndex;
						totalTableCount += futures.size();
					}
				}
			}
			log.info("service run all success, total migrate table count={}，total migrate data count={} ", totalTableCount, dbSwitchResult.getTotalRowCount());
			return dbSwitchResult;
		} finally {
			watch.stop();
			log.info("total ellipse = {} s", watch.getTotalTimeSeconds());

			StringBuilder sb = new StringBuilder();
			sb.append("===================================\n");
			sb.append(String.format("total ellipse time:\t %f s\n", watch.getTotalTimeSeconds()));
			sb.append("-------------------------------------\n");
			perfStats.forEach(st -> {
				sb.append(st);
				if (perfStats.size() > 1) {
					sb.append("-------------------------------------\n");
				}
			});
			sb.append("===================================\n");
			log.info("\n\n" + sb.toString());
		}
	}

	/**
	 * 构造一个异步执行任务
	 *
	 * @param td            表描述上下文
	 * @param indexInternal 源端索引号
	 * @param sds           源端的DataSource数据源
	 * @param tds           目的端的DataSource数据源
	 * @return CompletableFuture<Void>
	 */
	private CompletableFuture<Void> makeFutureTask(
			TableDescription td,
			Integer indexInternal,
			HikariDataSource sds,
			HikariDataSource tds,
			AtomicInteger numberOfFailures,
			AtomicLong currentSourceRowCount,
			DbSwitchResult dbSwitchResult) {
		return CompletableFuture.supplyAsync(getMigrateHandler(td, indexInternal, sds, tds))
				.exceptionally(getExceptHandler(td, indexInternal, numberOfFailures))
				.thenAccept((result) -> getAcceptHandler(result, currentSourceRowCount, dbSwitchResult));
	}

	/**
	 * 处理汇总结果
	 *
	 * @param tableResult
	 * @param switchResult
	 */
	private void getAcceptHandler(DbSwitchTableResult tableResult, AtomicLong currentSourceRowCount, DbSwitchResult switchResult) {

		currentSourceRowCount.getAndAdd(tableResult.getSyncCount().get());

		List<DbSwitchTableResult> tableResultList = switchResult.getTableResultList();
		AtomicBoolean ifAllSuccess = switchResult.getIfAllSuccess();
		AtomicLong totalRowCount = switchResult.getTotalRowCount();
		AtomicLong totalBytes = switchResult.getTotalBytes();
		AtomicLong totalTableCount = switchResult.getTotalTableCount();
		AtomicLong totalTableSuccessCount = switchResult.getTotalTableSuccessCount();
		AtomicLong totalTableFailCount = switchResult.getTotalTableFailCount();

		totalRowCount.getAndAdd(tableResult.getSyncCount().get());
		totalBytes.getAndAdd(tableResult.getSyncBytes().get());
		totalTableCount.getAndIncrement();
		if (!tableResult.getIfSuccess().get()) {
			ifAllSuccess.set(false);
			totalTableFailCount.getAndIncrement();
		} else {
			totalTableSuccessCount.getAndIncrement();
		}
		tableResultList.add(tableResult);
		//调用表结果处理器
		tableResultHandler.handler(tableResult);
	}


	/**
	 * 单表迁移处理方法
	 *
	 * @param td            表描述上下文
	 * @param indexInternal 源端索引号
	 * @param sds           源端的DataSource数据源
	 * @param tds           目的端的DataSource数据源
	 * @return Supplier<Long>
	 */
	private Supplier<DbSwitchTableResult> getMigrateHandler(
			TableDescription td,
			Integer indexInternal,
			HikariDataSource sds,
			HikariDataSource tds) {
		MigrationHandler instance = MigrationHandler.createInstance(td, properties, indexInternal, sds, tds);
		migrationHandlers.add(instance);
		return instance;
	}

	/**
	 * 异常处理函数方法
	 *
	 * @param td 表描述上下文
	 * @return Function<Throwable, Long>
	 */
	private Function<Throwable, DbSwitchTableResult> getExceptHandler(
			TableDescription td,
			Integer indexInternal, AtomicInteger numberOfFailures) {
		return (e) -> {
			log.error("Error migration for table: {}.{}, error message:", td.getSchemaName(),
					td.getTableName(), e);

			numberOfFailures.getAndIncrement();

			SourceDataSourceProperties sourceProperties = properties.getSource().get(indexInternal);
			String targetSchemaName = properties.getTarget().getTargetSchema();
			String targetTableName = PatterNameUtils.getFinalName(td.getTableName(), sourceProperties.getRegexTableMapper());
			DbSwitchTableResult dbSwitchTableResult = new DbSwitchTableResult();
			dbSwitchTableResult.setSourceSchemaName(td.getSchemaName());
			dbSwitchTableResult.setSourceTableName(td.getTableName());
			dbSwitchTableResult.setTargetSchemaName(targetSchemaName);
			dbSwitchTableResult.setTargetTableName(targetTableName);
			dbSwitchTableResult.getIfSuccess().set(false);
			dbSwitchTableResult.setSyncTime(new Date());
			dbSwitchTableResult.setSuccessMsg(null);
			dbSwitchTableResult.setErrorMsg(LogUtil.getError(e));
			return dbSwitchTableResult;
		};
	}

}
