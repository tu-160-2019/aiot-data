/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package net.srt.flink.core.job;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.srt.flink.common.assertion.Asserts;
import net.srt.flink.common.classloader.DinkyClassLoader;
import net.srt.flink.common.context.DinkyClassLoaderContextHolder;
import net.srt.flink.common.context.JarPathContextHolder;
import net.srt.flink.common.model.ProjectSystemConfiguration;
import net.srt.flink.common.result.ExplainResult;
import net.srt.flink.common.result.IResult;
import net.srt.flink.common.utils.LogUtil;
import net.srt.flink.common.utils.SqlUtil;
import net.srt.flink.common.utils.URLUtils;
import net.srt.flink.core.api.FlinkAPI;
import net.srt.flink.core.explainer.Explainer;
import net.srt.flink.core.result.ErrorResult;
import net.srt.flink.core.result.InsertResult;
import net.srt.flink.core.result.ResultBuilder;
import net.srt.flink.core.result.ResultPool;
import net.srt.flink.core.result.SelectResult;
import net.srt.flink.core.session.ExecutorEntity;
import net.srt.flink.core.session.SessionConfig;
import net.srt.flink.core.session.SessionInfo;
import net.srt.flink.core.session.SessionPool;
import net.srt.flink.executor.constant.FlinkSQLConstant;
import net.srt.flink.executor.executor.EnvironmentSetting;
import net.srt.flink.executor.executor.Executor;
import net.srt.flink.executor.executor.ExecutorSetting;
import net.srt.flink.executor.interceptor.FlinkInterceptor;
import net.srt.flink.executor.interceptor.FlinkInterceptorResult;
import net.srt.flink.executor.parser.SqlType;
import net.srt.flink.executor.trans.Operations;
import net.srt.flink.function.constant.PathConstant;
import net.srt.flink.function.data.model.Env;
import net.srt.flink.function.data.model.UDF;
import net.srt.flink.function.util.UDFUtil;
import net.srt.flink.gateway.Gateway;
import net.srt.flink.gateway.GatewayType;
import net.srt.flink.gateway.config.ActionType;
import net.srt.flink.gateway.config.FlinkConfig;
import net.srt.flink.gateway.config.GatewayConfig;
import net.srt.flink.gateway.result.GatewayResult;
import net.srt.flink.gateway.result.SavePointResult;
import net.srt.flink.gateway.result.TestResult;
import net.srt.flink.process.context.ProcessContextHolder;
import net.srt.flink.process.model.ProcessEntity;
import org.apache.flink.api.common.JobID;
import org.apache.flink.client.deployment.StandaloneClusterId;
import org.apache.flink.client.program.PackagedProgram;
import org.apache.flink.client.program.PackagedProgramUtils;
import org.apache.flink.client.program.rest.RestClusterClient;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.DeploymentOptions;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.PipelineOptions;
import org.apache.flink.configuration.RestOptions;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.streaming.api.environment.ExecutionCheckpointingOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.yarn.configuration.YarnConfigOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.srt.flink.function.util.UDFUtil.GATEWAY_TYPE_MAP;
import static net.srt.flink.function.util.UDFUtil.SESSION;
import static net.srt.flink.function.util.UDFUtil.YARN;

/**
 * JobManager
 *
 * @author wenmo
 * @since 2021/5/25 15:27
 **/
public class JobManager {

	private static final Logger logger = LoggerFactory.getLogger(JobManager.class);

	private JobHandler handler;
	private EnvironmentSetting environmentSetting;
	private ExecutorSetting executorSetting;
	private JobConfig config;
	private Executor executor;
	private Configuration configuration;
	private boolean useGateway = false;
	private boolean isPlanMode = false;
	private boolean useStatementSet = false;
	private boolean useRestAPI = false;
	private String sqlSeparator = FlinkSQLConstant.SEPARATOR;
	private GatewayType runMode = GatewayType.LOCAL;

	public JobManager() {
	}

	public void setUseGateway(boolean useGateway) {
		this.useGateway = useGateway;
	}

	public boolean isUseGateway() {
		return useGateway;
	}

	public void setPlanMode(boolean planMode) {
		isPlanMode = planMode;
	}

	public boolean isPlanMode() {
		return isPlanMode;
	}

	public boolean isUseStatementSet() {
		return useStatementSet;
	}

	public void setUseStatementSet(boolean useStatementSet) {
		this.useStatementSet = useStatementSet;
	}

	public boolean isUseRestAPI() {
		return useRestAPI;
	}

	public void setUseRestAPI(boolean useRestAPI) {
		this.useRestAPI = useRestAPI;
	}

	public String getSqlSeparator() {
		return sqlSeparator;
	}

	public void setSqlSeparator(String sqlSeparator) {
		this.sqlSeparator = sqlSeparator;
	}

	public JobManager(JobConfig config) {
		this.config = config;
	}

	public static JobManager build() {
		JobManager manager = new JobManager();
		manager.init();
		return manager;
	}

	public static JobManager build(JobConfig config) {
		ProcessEntity process = ProcessContextHolder.getProcess();
		try {
			initGatewayConfig(config);
			JobManager manager = new JobManager(config);
			manager.init();
			return manager;
		} catch (Exception e) {
			process.error(LogUtil.getError(e));
			process.infoEnd();
			ProcessContextHolder.clear();
			throw new RuntimeException(e.getMessage());
		}
	}

	public static JobManager buildPlanMode(JobConfig config) {
		JobManager manager = new JobManager(config);
		manager.setPlanMode(true);
		manager.init();
		ProcessContextHolder.getProcess().info("Build Flink plan mode success.");
		return manager;
	}

	private static void initGatewayConfig(JobConfig config) {
		if (useGateway(config.getType())) {
			Asserts.checkNull(config.getGatewayConfig(), "GatewayConfig 不能为空");
			config.getGatewayConfig().setType(GatewayType.get(config.getType()));
			config.getGatewayConfig().setTaskId(config.getTaskId());
			config.getGatewayConfig().getFlinkConfig().setJobName(config.getJobName());
			config.getGatewayConfig().getFlinkConfig().setSavePoint(config.getSavePointPath());
			config.setUseRemote(false);
		}
	}

	public static boolean useGateway(String type) {
		return (GatewayType.YARN_PER_JOB.equalsValue(type) || GatewayType.YARN_APPLICATION.equalsValue(type)
				|| GatewayType.KUBERNETES_APPLICATION.equalsValue(type));
	}

	private Executor createExecutor() {
		initEnvironmentSetting();
		if (!runMode.equals(GatewayType.LOCAL) && !useGateway && config.isUseRemote()) {
			executor = Executor.buildRemoteExecutor(environmentSetting, config.getExecutorSetting());
			return executor;
		} else {
			if (ArrayUtil.isNotEmpty(config.getJarFiles())) {
				config.getExecutorSetting().getConfig().put(PipelineOptions.JARS.key(),
						Stream.of(config.getJarFiles()).map(FileUtil::getAbsolutePath)
								.collect(Collectors.joining(",")));
			}

			executor = Executor.buildLocalExecutor(config.getExecutorSetting());
			return executor;
		}
	}

	private Executor createExecutorWithSession() {
		if (config.isUseSession()) {
			ExecutorEntity executorEntity = SessionPool.get(config.getSession());
			if (Asserts.isNotNull(executorEntity)) {
				executor = executorEntity.getExecutor();
				config.setSessionConfig(executorEntity.getSessionConfig());
				initEnvironmentSetting();
				executor.update(executorSetting);
			} else {
				createExecutor();
				SessionPool.push(new ExecutorEntity(config.getSession(), executor));
			}
		} else {
			createExecutor();
		}
		executor.getSqlManager().registerSqlFragment(config.getVariables());
		return executor;
	}

	private void initEnvironmentSetting() {
		if (Asserts.isNotNullString(config.getAddress())) {
			environmentSetting = EnvironmentSetting.build(config.getAddress(), config.getJarFiles());
		}
	}

	private void initExecutorSetting() {
		executorSetting = config.getExecutorSetting();
	}

	public boolean init() {
		ProcessEntity process = ProcessContextHolder.getProcess();
		if (!isPlanMode) {
			runMode = GatewayType.get(config.getType());
			useGateway = useGateway(config.getType());
			handler = JobHandler.build();
		}
		useStatementSet = config.isUseStatementSet();
		//zrx ProjectSystemConfiguration
		useRestAPI = ProjectSystemConfiguration.getByProjectId(process.getProjectId()).isUseRestAPI();
		sqlSeparator = ProjectSystemConfiguration.getByProjectId(process.getProjectId()).getSqlSeparator();

		initExecutorSetting();
		createExecutorWithSession();

		return false;
	}

	private void addConfigurationClsAndJars(List<URL> jarList, List<URL> classpaths)
			throws Exception {
		Field configuration = StreamExecutionEnvironment.class.getDeclaredField("configuration");
		configuration.setAccessible(true);
		Configuration o =
				(Configuration) configuration.get(executor.getStreamExecutionEnvironment());

		Field confData = Configuration.class.getDeclaredField("confData");
		confData.setAccessible(true);
		Map<String, Object> temp = (Map<String, Object>) confData.get(o);
		temp.put(
				PipelineOptions.CLASSPATHS.key(),
				classpaths.stream().map(URL::toString).collect(Collectors.toList()));
		temp.put(
				PipelineOptions.JARS.key(),
				jarList.stream().map(URL::toString).collect(Collectors.toList()));
	}

	public void initUDF(List<UDF> udfList) {
		if (Asserts.isNotNullCollection(udfList)) {
			initUDF(udfList, runMode, config.getTaskId());
		}
	}

	public void initUDF(List<UDF> udfList, GatewayType runMode, Integer taskId) {
		if (taskId == null) {
			taskId = -RandomUtil.randomInt(0, 1000);
		}
		ProcessEntity process = ProcessContextHolder.getProcess();

		// 这里要分开
		// 1. 得到jar包路径，注入remote环境
		Set<File> jarFiles = JarPathContextHolder.getUdfFile();

		Set<File> otherPluginsFiles = JarPathContextHolder.getOtherPluginsFiles();
		jarFiles.addAll(otherPluginsFiles);

		List<File> udfJars =
				Arrays.stream(UDFUtil.initJavaUDF(udfList, runMode, taskId))
						.map(File::new)
						.collect(Collectors.toList());
		jarFiles.addAll(udfJars);

		String[] jarPaths =
				CollUtil.removeNull(jarFiles).stream()
						.map(File::getAbsolutePath)
						.toArray(String[]::new);

		if (GATEWAY_TYPE_MAP.get(SESSION).contains(runMode)) {
			config.setJarFiles(jarPaths);
		}
		try {
			List<URL> jarList = CollUtil.newArrayList(URLUtils.getURLs(jarFiles));
			writeManifest(taskId, jarList);

			addConfigurationClsAndJars(
					jarList, CollUtil.newArrayList(URLUtils.getURLs(otherPluginsFiles)));
		} catch (Exception e) {
			logger.error("add configuration failed;reason:{}", LogUtil.getError(e));
			throw new RuntimeException(e);
		}

		// 2.编译python
		String[] pyPaths =
				UDFUtil.initPythonUDF(
						udfList,
						runMode,
						config.getTaskId(),
						executor.getTableConfig().getConfiguration());

		executor.initUDF(jarPaths);

		executor.initPyUDF(Env.getPath(), pyPaths);
		if (GATEWAY_TYPE_MAP.get(YARN).contains(runMode)) {
			config.getGatewayConfig().setJarPaths(ArrayUtil.append(jarPaths, pyPaths));
		}
		process.info(StrUtil.format("A total of {} UDF have been Init.", udfList.size()));
		process.info("Initializing Flink UDF...Finish");
	}

	private void writeManifest(Integer taskId, List<URL> jarPaths) {
		JSONArray array =
				jarPaths.stream().map(URL::getFile).collect(Collectors.toCollection(JSONArray::new));
		JSONObject object = new JSONObject();
		object.set("jars", array);
		FileUtil.writeUtf8String(
				object.toStringPretty(),
				PathConstant.getUdfPackagePath(taskId) + PathConstant.DEP_MANIFEST);
	}

	private boolean ready() {
		return handler.init();
	}

	private boolean success() {
		return handler.success();
	}

	private boolean failed() {
		return handler.failed();
	}

	public boolean close() {
		JobContextHolder.clear();
		return false;
	}

	public void initClassLoader(JobConfig config) {
		if (CollUtil.isNotEmpty(config.getConfig())) {
			String pipelineJars = config.getConfig().get(PipelineOptions.JARS.key());
			String classpaths = config.getConfig().get(PipelineOptions.CLASSPATHS.key());
			// add custom jar path
			if (StrUtil.isNotBlank(pipelineJars)) {
				String[] paths = pipelineJars.split(",");
				for (String path : paths) {
					File file = FileUtil.file(path);
					if (!file.exists()) {
						throw new RuntimeException("file: " + path + " .not exists! ");
					}
					JarPathContextHolder.addUdfPath(file);
				}
			}
			// add custom classpath
			if (StrUtil.isNotBlank(classpaths)) {
				String[] paths = pipelineJars.split(",");
				for (String path : paths) {
					File file = FileUtil.file(path);
					if (!file.exists()) {
						throw new RuntimeException("file: " + path + " .not exists! ");
					}
					JarPathContextHolder.addOtherPlugins(file);
				}
			}
		}

		DinkyClassLoader classLoader =
				new DinkyClassLoader(
						CollUtil.addAll(
								JarPathContextHolder.getUdfFile(),
								JarPathContextHolder.getOtherPluginsFiles()),
						Thread.currentThread().getContextClassLoader());
		DinkyClassLoaderContextHolder.set(classLoader);
	}

	public JobResult executeSql(String statement) {
		initClassLoader(config);
		ProcessEntity process = ProcessContextHolder.getProcess();
		Job job = Job.init(runMode, config, executorSetting, executor, statement, useGateway);
		job.setNodeRecordId(process.getNodeRecordId());
		if (!useGateway) {
			job.setJobManagerAddress(environmentSetting.getAddress());
		}
		JobContextHolder.setJob(job);
		ready();
		boolean hasReady = true;
		String currentSql = "";
		JobParam jobParam = Explainer.build(executor, useStatementSet, sqlSeparator)
				.pretreatStatements(SqlUtil.getStatements(statement, sqlSeparator));
		try {
			initUDF(jobParam.getUdfList(), runMode, config.getTaskId());

			for (StatementParam item : jobParam.getDdl()) {
				currentSql = item.getValue();
				executor.executeSql(item.getValue());
			}
			if (!jobParam.getTrans().isEmpty()) {
				// Use statement set or gateway only submit inserts.
				if (useStatementSet && useGateway) {
					List<String> inserts = new ArrayList<>();
					for (StatementParam item : jobParam.getTrans()) {
						if (item.getType().isInsert()) {
							inserts.add(item.getValue());
						}
					}
					if (!inserts.isEmpty()) {
						// Use statement set need to merge all insert sql into a sql.
						currentSql = String.join(sqlSeparator, inserts);
						//zrx
						job.setExecuteSql(currentSql);
						GatewayResult gatewayResult = submitByGateway(inserts);
						// Use statement set only has one jid.
						job.setResult(InsertResult.success(gatewayResult.getAppId()));
						job.setJobId(gatewayResult.getAppId());
						job.setJids(gatewayResult.getJids());
						job.setJobManagerAddress(formatAddress(gatewayResult.getWebURL()));
					} else {
						job.setEndByNoInsert(true);
					}
					job.setEndTime(LocalDateTime.now());
					job.setStatus(Job.JobStatus.SUCCESS);
					success();
					hasReady = false;
				} else if (useStatementSet) {
					List<String> inserts = new ArrayList<>();
					for (StatementParam item : jobParam.getTrans()) {
						if (item.getType().isInsert()) {
							inserts.add(item.getValue());
						}
					}
					if (inserts.size() > 0) {
						currentSql = String.join(sqlSeparator, inserts);
						//zrx
						job.setExecuteSql(currentSql);
						// Remote mode can get the table result.
						TableResult tableResult = executor.executeStatementSet(inserts);
						if (tableResult.getJobClient().isPresent()) {
							job.setJobId(tableResult.getJobClient().get().getJobID().toHexString());
							job.setJids(new ArrayList<String>() {

								{
									add(job.getJobId());
								}
							});
						}
						if (config.isUseResult()) {
							// Build insert result.
							IResult result = ResultBuilder
									.build(SqlType.INSERT, config.getMaxRowNum(), config.isUseChangeLog(),
											config.isUseAutoCancel(), executor.getTimeZone())
									.getResult(tableResult);
							job.setResult(result);
						}
					}
					job.setEndTime(LocalDateTime.now());
					job.setStatus(Job.JobStatus.SUCCESS);
					success();
					hasReady = false;
				} else if (useGateway) {
					List<String> inserts = new ArrayList<>();
					for (StatementParam item : jobParam.getTrans()) {
						inserts.add(item.getValue());
						// Only can submit the first of insert sql, when not use statement set.
						// zrx
						//break;
					}
					currentSql = String.join(sqlSeparator, inserts);
					//zrx
					job.setExecuteSql(currentSql);
					GatewayResult gatewayResult = submitByGateway(inserts);
					job.setResult(InsertResult.success(gatewayResult.getAppId()));
					job.setJobId(gatewayResult.getAppId());
					job.setJids(gatewayResult.getJids());
					job.setJobManagerAddress(formatAddress(gatewayResult.getWebURL()));
					job.setEndTime(LocalDateTime.now());
					job.setStatus(Job.JobStatus.SUCCESS);
					success();
					hasReady = false;
				} else {
					int i = 0;
					for (StatementParam item : jobParam.getTrans()) {
						if (!hasReady) {
							ready();
						}
						hasReady = false;
						i++;
						currentSql = item.getValue();
						//zrx 一个sql相当于一个任务
						job.setExecuteSql(currentSql);
						FlinkInterceptorResult flinkInterceptorResult = FlinkInterceptor.build(executor,
								item.getValue());
						if (Asserts.isNotNull(flinkInterceptorResult.getTableResult())) {
							//只取最后一个结果
							if (config.isUseResult() && i == jobParam.getTrans().size()) {
								IResult result = ResultBuilder
										.build(item.getType(), config.getMaxRowNum(), config.isUseChangeLog(),
												config.isUseAutoCancel(), executor.getTimeZone())
										.getResult(flinkInterceptorResult.getTableResult());
								job.setResult(result);
							}
						} else {
							if (!flinkInterceptorResult.isNoExecute()) {
								TableResult tableResult = executor.executeSql(item.getValue());
								if (tableResult.getJobClient().isPresent()) {
									job.setJobId(tableResult.getJobClient().get().getJobID().toHexString());
									job.setJids(new ArrayList<String>() {
										{
											add(job.getJobId());
										}
									});
								}
								//只取最后一个结果
								if (config.isUseResult() && i == jobParam.getTrans().size()) {
									IResult result = ResultBuilder.build(item.getType(), config.getMaxRowNum(),
											config.isUseChangeLog(), config.isUseAutoCancel(),
											executor.getTimeZone()).getResult(tableResult);
									job.setResult(result);
								}
							}
						}
						job.setEndTime(LocalDateTime.now());
						job.setStatus(Job.JobStatus.SUCCESS);
						success();
						// Only can submit the first of insert sql, when not use statement set.
						// zrx
						//break;
					}
				}
			}
			if (!jobParam.getExecute().isEmpty()) {
				if (useGateway) {
					if (!hasReady) {
						ready();
					}
					List<String> sqls = new ArrayList<>();
					String lastSql = "";
					for (StatementParam item : jobParam.getExecute()) {
						currentSql = item.getValue();
						executor.executeSql(item.getValue());
						sqls.add(item.getValue());
						//zrx
						/*if (!useStatementSet) {
							break;
						}*/
					}
					lastSql = String.join(sqlSeparator, sqls);
					//zrx
					job.setExecuteSql(lastSql);
					GatewayResult gatewayResult;
					config.addGatewayConfig(executor.getSetConfig());
					if (runMode.isApplicationMode()) {
						gatewayResult = Gateway.build(config.getGatewayConfig()).submitJar();
					} else {
						StreamGraph streamGraph = executor.getStreamGraph();
						streamGraph.setJobName(config.getJobName());
						JobGraph jobGraph = streamGraph.getJobGraph();
						if (Asserts.isNotNullString(config.getSavePointPath())) {
							jobGraph.setSavepointRestoreSettings(
									SavepointRestoreSettings.forPath(config.getSavePointPath(), true));
						}
						gatewayResult = Gateway.build(config.getGatewayConfig()).submitJobGraph(jobGraph);
					}
					job.setResult(InsertResult.success(gatewayResult.getAppId()));
					job.setJobId(gatewayResult.getAppId());
					job.setJids(gatewayResult.getJids());
					job.setJobManagerAddress(formatAddress(gatewayResult.getWebURL()));

					job.setEndTime(LocalDateTime.now());
					job.setStatus(Job.JobStatus.SUCCESS);
					success();
				} else {
					if (!hasReady) {
						ready();
					}
					String lastSql = "";
					List<String> sqls = new ArrayList<>();
					for (StatementParam item : jobParam.getExecute()) {
						currentSql = item.getValue();
						executor.executeSql(item.getValue());
						sqls.add(item.getValue());
						//zrx
						/*if (!useStatementSet) {
							break;
						}*/
					}
					lastSql = String.join(sqlSeparator, sqls);
					//zrx
					job.setExecuteSql(lastSql);
					JobClient jobClient = executor.executeAsync(config.getJobName());
					if (Asserts.isNotNull(jobClient)) {
						job.setJobId(jobClient.getJobID().toHexString());
						job.setJids(new ArrayList<String>() {
							{
								add(job.getJobId());
							}
						});
					}
					if (config.isUseResult()) {
						IResult result = ResultBuilder
								.build(SqlType.EXECUTE, config.getMaxRowNum(), config.isUseChangeLog(),
										config.isUseAutoCancel(), executor.getTimeZone())
								.getResult(null);
						job.setResult(result);
					}

					job.setEndTime(LocalDateTime.now());
					job.setStatus(Job.JobStatus.SUCCESS);
					success();
				}
			}
			/*job.setEndTime(LocalDateTime.now());
			job.setStatus(Job.JobStatus.SUCCESS);
			success();*/
		} catch (Exception e) {
			String error = LogUtil.getError("Exception in executing FlinkSQL:\n" + currentSql, e);
			job.setEndTime(LocalDateTime.now());
			job.setStatus(Job.JobStatus.FAILED);
			job.setError(error);
			process.error(error);
			failed();
		} finally {
			close();
		}
		return job.getJobResult();
	}

	private GatewayResult submitByGateway(List<String> inserts) {
		GatewayResult gatewayResult = null;

		// Use gateway need to build gateway config, include flink configeration.
		config.addGatewayConfig(executor.getSetConfig());

		if (runMode.isApplicationMode()) {
			// Application mode need to submit dlink-app.jar that in the hdfs or image.
			gatewayResult = Gateway.build(config.getGatewayConfig()).submitJar();
		} else {
			JobGraph jobGraph = executor.getJobGraphFromInserts(inserts);
			// Perjob mode need to set savepoint restore path, when recovery from savepoint.
			if (Asserts.isNotNullString(config.getSavePointPath())) {
				jobGraph.setSavepointRestoreSettings(SavepointRestoreSettings.forPath(config.getSavePointPath(), true));
			}
			// Perjob mode need to submit job graph.
			gatewayResult = Gateway.build(config.getGatewayConfig()).submitJobGraph(jobGraph);
		}
		return gatewayResult;
	}

	private String formatAddress(String webURL) {
		if (Asserts.isNotNullString(webURL)) {
			return webURL.replaceAll("http://", "");
		} else {
			return "";
		}
	}

	public IResult executeDDL(String statement) {
		String[] statements = SqlUtil.getStatements(statement, sqlSeparator);
		try {
			IResult result = null;
			for (String item : statements) {
				String newStatement = executor.pretreatStatement(item);
				if (newStatement.trim().isEmpty()) {
					continue;
				}
				SqlType operationType = Operations.getOperationType(newStatement);
				if (SqlType.INSERT == operationType || SqlType.SELECT == operationType) {
					continue;
				}
				LocalDateTime startTime = LocalDateTime.now();
				TableResult tableResult = executor.executeSql(newStatement);
				result = ResultBuilder.build(operationType, config.getMaxRowNum(), false, false, executor.getTimeZone())
						.getResult(tableResult);
				result.setStartTime(startTime);
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ErrorResult();
	}

	public static SelectResult getJobData(String jobId) {
		return ResultPool.get(jobId);
	}

	public static SessionInfo createSession(String session, SessionConfig sessionConfig, String createUser) {
		if (SessionPool.exist(session)) {
			return SessionPool.getInfo(session);
		}
		Executor sessionExecutor = null;
		if (sessionConfig.isUseRemote()) {
			sessionExecutor = Executor.buildRemoteExecutor(EnvironmentSetting.build(sessionConfig.getAddress()),
					ExecutorSetting.DEFAULT);
		} else {
			sessionExecutor = Executor.buildLocalExecutor(sessionConfig.getExecutorSetting());
		}
		ExecutorEntity executorEntity = new ExecutorEntity(session, sessionConfig, createUser, LocalDateTime.now(),
				sessionExecutor);
		SessionPool.push(executorEntity);
		return SessionInfo.build(executorEntity);
	}

	public static List<SessionInfo> listSession(String createUser) {
		return SessionPool.filter(createUser);
	}

	public ExplainResult explainSql(String statement) {
		return Explainer.build(executor, useStatementSet, sqlSeparator)
				.initialize(this, config, statement).explainSql(statement);
	}

	public ObjectNode getStreamGraph(String statement) {
		return Explainer.build(executor, useStatementSet, sqlSeparator).initialize(this, config, statement).getStreamGraph(statement);
	}

	public String getJobPlanJson(String statement) {
		return Explainer.build(executor, useStatementSet, sqlSeparator).initialize(this, config, statement).getJobPlanInfo(statement).getJsonPlan();
	}

	public boolean cancel(String jobId) {
		if (useGateway && !useRestAPI) {
			config.getGatewayConfig().setFlinkConfig(FlinkConfig.build(jobId, ActionType.CANCEL.getValue(),
					null, null));
			Gateway.build(config.getGatewayConfig()).savepointJob();
			return true;
		} else {
			try {
				return FlinkAPI.build(config.getAddress()).stop(jobId);
			} catch (Exception e) {
				logger.error("停止作业时集群不存在: " + e);
			}
			return false;
		}
	}

	public SavePointResult savepoint(String jobId, String savePointType, String savePoint) {
		if (useGateway && !useRestAPI) {
			config.getGatewayConfig().setFlinkConfig(FlinkConfig.build(jobId, ActionType.SAVEPOINT.getValue(),
					savePointType, null));
			return Gateway.build(config.getGatewayConfig()).savepointJob(savePoint);
		} else {
			return FlinkAPI.build(config.getAddress()).savepoints(jobId, savePointType);
		}
	}

	public JobResult executeJar() {
		ProcessEntity process = ProcessContextHolder.getProcess();
		Job job = Job.init(runMode, config, executorSetting, executor, null, useGateway);
		job.setNodeRecordId(process.getNodeRecordId());
		JobContextHolder.setJob(job);
		ready();
		if (GatewayType.LOCAL.equalsValue(config.getType()) || GatewayType.STANDALONE.equalsValue(config.getType())) {
			try {
				// 集群信息
				Configuration configuration = new Configuration();
				configuration.setString(JobManagerOptions.ADDRESS, config.getJobManageAddress());
				configuration.setInteger(JobManagerOptions.PORT, config.getJobManagePort());
				configuration.setInteger(RestOptions.PORT, Integer.parseInt(config.getRestPort()));
				RestClusterClient<StandaloneClusterId> client = new RestClusterClient<>(configuration, StandaloneClusterId.getInstance());
				File jarFile = new File(config.getJarPath());
				SavepointRestoreSettings savepointRestoreSettings = SavepointRestoreSettings.none();
				if (Asserts.isNotNullString(config.getSavePointPath())) {
					savepointRestoreSettings = SavepointRestoreSettings.forPath(config.getSavePointPath(), true);
				}
				//构建提交任务参数
				PackagedProgram program = PackagedProgram
						.newBuilder()
						.setConfiguration(configuration)
						.setEntryPointClassName(config.getEntryPointClassName())
						.setJarFile(jarFile)
						.setArguments(Asserts.isNotNullString(config.getJarParams()) ? config.getJarParams().split(" ") : new String[]{})
						.setSavepointRestoreSettings(savepointRestoreSettings).build();
				//创建任务
				JobGraph jobGraph = PackagedProgramUtils.createJobGraph(program, configuration, config.getParallelism(), false);
				//提交任务
				CompletableFuture<JobID> result = client.submitJob(jobGraph);
				JobID jobId = result.get();
				job.setJobId(jobId.toHexString());
				job.setJids(new ArrayList<String>() {
					{
						add(job.getJobId());
					}
				});
				job.setEndTime(LocalDateTime.now());
				job.setStatus(Job.JobStatus.SUCCESS);
				success();
			} catch (Exception e) {
				String error = LogUtil.getError(
						"Exception in executing Jar：\n" + config.getJarPath(), e);
				job.setEndTime(LocalDateTime.now());
				job.setStatus(Job.JobStatus.FAILED);
				job.setError(error);
				failed();
				process.error(error);
			} finally {
				close();
			}
		} else {
			try {
				GatewayResult gatewayResult = Gateway.build(config.getGatewayConfig()).submitJar();
				job.setResult(InsertResult.success(gatewayResult.getAppId()));
				job.setJobId(gatewayResult.getAppId());
				job.setJids(gatewayResult.getJids());
				job.setJobManagerAddress(formatAddress(gatewayResult.getWebURL()));
				job.setEndTime(LocalDateTime.now());
				job.setStatus(Job.JobStatus.SUCCESS);
				success();
			} catch (Exception e) {
				String error = LogUtil.getError(
						"Exception in executing Jar：\n" + config.getGatewayConfig().getAppConfig().getUserJarPath(), e);
				job.setEndTime(LocalDateTime.now());
				job.setStatus(Job.JobStatus.FAILED);
				job.setError(error);
				failed();
				process.error(error);
			} finally {
				close();
			}
		}
		return job.getJobResult();
	}

	public static TestResult testGateway(GatewayConfig gatewayConfig) {
		return Gateway.build(gatewayConfig).test();
	}

	public String exportSql(String sql) {
		String statement = executor.pretreatStatement(sql);
		StringBuilder sb = new StringBuilder();
		if (Asserts.isNotNullString(config.getJobName())) {
			sb.append("set " + PipelineOptions.NAME.key() + " = " + config.getJobName() + ";\r\n");
		}
		if (Asserts.isNotNull(config.getParallelism())) {
			sb.append("set " + CoreOptions.DEFAULT_PARALLELISM.key() + " = " + config.getParallelism() + ";\r\n");
		}
		if (Asserts.isNotNull(config.getCheckpoint())) {
			sb.append("set " + ExecutionCheckpointingOptions.CHECKPOINTING_INTERVAL.key() + " = "
					+ config.getCheckpoint() + ";\r\n");
		}
		if (Asserts.isNotNullString(config.getSavePointPath())) {
			sb.append("set " + SavepointConfigOptions.SAVEPOINT_PATH + " = " + config.getSavePointPath() + ";\r\n");
		}
		if (Asserts.isNotNull(config.getGatewayConfig())
				&& Asserts.isNotNull(config.getGatewayConfig().getFlinkConfig().getConfiguration())) {
			for (Map.Entry<String, String> entry : config.getGatewayConfig().getFlinkConfig().getConfiguration()
					.entrySet()) {
				sb.append("set " + entry.getKey() + " = " + entry.getValue() + ";\r\n");
			}
		}

		switch (GatewayType.get(config.getType())) {
			case YARN_PER_JOB:
			case YARN_APPLICATION:
				sb.append("set " + DeploymentOptions.TARGET.key() + " = "
						+ GatewayType.get(config.getType()).getLongValue() + ";\r\n");
				if (Asserts.isNotNull(config.getGatewayConfig())) {
					sb.append("set " + YarnConfigOptions.PROVIDED_LIB_DIRS.key() + " = "
							+ Collections.singletonList(config.getGatewayConfig().getClusterConfig().getFlinkLibPath())
							+ ";\r\n");
				}
				if (Asserts.isNotNull(config.getGatewayConfig())
						&& Asserts.isNotNullString(config.getGatewayConfig().getFlinkConfig().getJobName())) {
					sb.append("set " + YarnConfigOptions.APPLICATION_NAME.key() + " = "
							+ config.getGatewayConfig().getFlinkConfig().getJobName() + ";\r\n");
				}
				break;
			default:
		}
		sb.append(statement);
		return sb.toString();
	}

	public Executor getExecutor() {
		return executor;
	}
}
