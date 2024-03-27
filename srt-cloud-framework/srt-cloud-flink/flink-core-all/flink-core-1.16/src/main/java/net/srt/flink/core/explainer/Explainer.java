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

package net.srt.flink.core.explainer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.srt.flink.client.base.model.LineageRel;
import net.srt.flink.common.assertion.Asserts;
import net.srt.flink.common.context.DinkyClassLoaderContextHolder;
import net.srt.flink.common.context.JarPathContextHolder;
import net.srt.flink.common.model.ProjectSystemConfiguration;
import net.srt.flink.common.result.ExplainResult;
import net.srt.flink.common.result.SqlExplainResult;
import net.srt.flink.common.utils.LogUtil;
import net.srt.flink.common.utils.SqlUtil;
import net.srt.flink.common.utils.URLUtils;
import net.srt.flink.core.job.JobConfig;
import net.srt.flink.core.job.JobManager;
import net.srt.flink.core.job.JobParam;
import net.srt.flink.core.job.StatementParam;
import net.srt.flink.executor.constant.FlinkSQLConstant;
import net.srt.flink.executor.executor.Executor;
import net.srt.flink.executor.interceptor.FlinkInterceptor;
import net.srt.flink.executor.parser.AddJarSqlParser;
import net.srt.flink.executor.parser.SqlType;
import net.srt.flink.executor.trans.Operations;
import net.srt.flink.function.data.model.UDF;
import net.srt.flink.function.util.UDFUtil;
import net.srt.flink.process.context.ProcessContextHolder;
import net.srt.flink.process.model.ProcessEntity;
import org.apache.flink.runtime.rest.messages.JobPlanInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Explainer
 *
 * @author zrx
 * @since 2021/6/22
 **/
public class Explainer {

	private Executor executor;
	private boolean useStatementSet;
	private String sqlSeparator = FlinkSQLConstant.SEPARATOR;
	private ObjectMapper mapper = new ObjectMapper();

	public Explainer(Executor executor) {
		this.executor = executor;
		this.useStatementSet = true;
		init();
	}

	public Explainer(Executor executor, boolean useStatementSet) {
		this.executor = executor;
		this.useStatementSet = useStatementSet;
		init();
	}

	public Explainer(Executor executor, boolean useStatementSet, String sqlSeparator) {
		this.executor = executor;
		this.useStatementSet = useStatementSet;
		this.sqlSeparator = sqlSeparator;
	}

	public void init() {
		//zrx ProjectSystemConfiguration
		ProcessEntity process = ProcessContextHolder.getProcess();
		sqlSeparator = ProjectSystemConfiguration.getByProjectId(process.getProjectId()).getSqlSeparator();
	}

	public static Explainer build(Executor executor) {
		return new Explainer(executor, false, ";");
	}

	public static Explainer build(Executor executor, boolean useStatementSet, String sqlSeparator) {
		return new Explainer(executor, useStatementSet, sqlSeparator);
	}

	public Explainer initialize(JobManager jobManager, JobConfig config, String statement) {
		jobManager.initClassLoader(config);
		String[] statements = SqlUtil.getStatements(SqlUtil.removeNote(statement), sqlSeparator);
		jobManager.initUDF(parseUDFFromStatements(statements));
		return this;
	}

	public JobParam pretreatStatements(String[] statements) {
		List<StatementParam> ddl = new ArrayList<>();
		List<StatementParam> trans = new ArrayList<>();
		List<StatementParam> execute = new ArrayList<>();
		List<String> statementList = new ArrayList<>();
		List<UDF> udfList = new ArrayList<>();
		for (String item : statements) {
			String statement = executor.pretreatStatement(item);
			if (statement.isEmpty()) {
				continue;
			}
			SqlType operationType = Operations.getOperationType(statement);
			if (operationType.equals(SqlType.ADD)) {
				AddJarSqlParser.getAllFilePath(statement).forEach(JarPathContextHolder::addOtherPlugins);
				DinkyClassLoaderContextHolder.get()
						.addURL(URLUtils.getURLs(JarPathContextHolder.getOtherPluginsFiles()));
			} else if (operationType.equals(SqlType.INSERT) || operationType.equals(SqlType.SELECT)
					|| operationType.equals(SqlType.SHOW)
					|| operationType.equals(SqlType.DESCRIBE) || operationType.equals(SqlType.DESC)) {
				trans.add(new StatementParam(statement, operationType));
				statementList.add(statement);
				//zrx
				/*if (!useStatementSet) {
					break;
				}*/
			} else if (operationType.equals(SqlType.EXECUTE)) {
				execute.add(new StatementParam(statement, operationType));
			} else {
				UDF udf = UDFUtil.toUDF(statement);
				if (Asserts.isNotNull(udf)) {
					udfList.add(UDFUtil.toUDF(statement));
				}
				ddl.add(new StatementParam(statement, operationType));
				statementList.add(statement);
			}
		}
		return new JobParam(statementList, ddl, trans, execute, CollUtil.removeNull(udfList));
	}

	public List<UDF> parseUDFFromStatements(String[] statements) {
		List<UDF> udfList = new ArrayList<>();
		for (String statement : statements) {
			if (statement.isEmpty()) {
				continue;
			}
			UDF udf = UDFUtil.toUDF(statement);
			if (Asserts.isNotNull(udf)) {
				udfList.add(UDFUtil.toUDF(statement));
			}
		}
		return udfList;
	}

	public List<SqlExplainResult> explainSqlResult(String statement) {
		String[] sqls = SqlUtil.getStatements(statement, sqlSeparator);
		List<SqlExplainResult> sqlExplainRecords = new ArrayList<>();
		int index = 1;
		for (String item : sqls) {
			SqlExplainResult record = new SqlExplainResult();
			String sql = "";
			try {
				sql = FlinkInterceptor.pretreatStatement(executor, item, sqlSeparator);
				if (Asserts.isNullString(sql)) {
					continue;
				}
				SqlType operationType = Operations.getOperationType(sql);
				if (operationType.equals(SqlType.INSERT) || operationType.equals(SqlType.SELECT)) {
					record = executor.explainSqlRecord(sql);
					if (Asserts.isNull(record)) {
						continue;
					}
				} else {
					record = executor.explainSqlRecord(sql);
					if (Asserts.isNull(record)) {
						continue;
					}
					executor.executeSql(sql);
				}
			} catch (Exception e) {
				e.printStackTrace();
				record.setError(e.getMessage());
				record.setExplainTrue(false);
				record.setExplainTime(LocalDateTime.now());
				record.setSql(sql);
				record.setIndex(index);
				sqlExplainRecords.add(record);
				break;
			}
			record.setExplainTrue(true);
			record.setExplainTime(LocalDateTime.now());
			record.setSql(sql);
			record.setIndex(index++);
			sqlExplainRecords.add(record);
		}
		return sqlExplainRecords;
	}

	public ExplainResult explainSql(String statement) {
		ProcessEntity process = ProcessContextHolder.getProcess();
		process.info("Start explain FlinkSQL...");
		JobParam jobParam = pretreatStatements(SqlUtil.getStatements(statement, sqlSeparator));
		List<SqlExplainResult> sqlExplainRecords = new ArrayList<>();
		int index = 1;
		boolean correct = true;
		for (StatementParam item : jobParam.getDdl()) {
			SqlExplainResult record = new SqlExplainResult();
			try {
				record = executor.explainSqlRecord(item.getValue());
				if (Asserts.isNull(record)) {
					continue;
				}
				executor.executeSql(item.getValue());
			} catch (Exception e) {
				String error = LogUtil.getError(e);
				record.setError(error);
				record.setExplainTrue(false);
				record.setExplainTime(LocalDateTime.now());
				record.setSql(item.getValue());
				record.setIndex(index);
				sqlExplainRecords.add(record);
				correct = false;
				process.error(error);
				break;
			}
			record.setExplainTrue(true);
			record.setExplainTime(LocalDateTime.now());
			record.setSql(item.getValue());
			record.setIndex(index++);
			sqlExplainRecords.add(record);
		}
		if (correct && jobParam.getTrans().size() > 0) {
			if (useStatementSet) {
				SqlExplainResult record = new SqlExplainResult();
				List<String> inserts = new ArrayList<>();
				for (StatementParam item : jobParam.getTrans()) {
					if (item.getType().equals(SqlType.INSERT)) {
						inserts.add(item.getValue());
					}
				}
				if (inserts.size() > 0) {
					String sqlSet = String.join(";\r\n ", inserts);
					try {
						record.setExplain(executor.explainStatementSet(inserts));
						record.setParseTrue(true);
						record.setExplainTrue(true);
					} catch (Exception e) {
						String error = LogUtil.getError(e);
						record.setError(error);
						record.setParseTrue(false);
						record.setExplainTrue(false);
						correct = false;
						process.error(error);
					} finally {
						record.setType("Modify DML");
						record.setExplainTime(LocalDateTime.now());
						record.setSql(sqlSet);
						record.setIndex(index);
						sqlExplainRecords.add(record);
					}
				}
			} else {
				for (StatementParam item : jobParam.getTrans()) {
					SqlExplainResult record = new SqlExplainResult();
					try {
						record = executor.explainSqlRecord(item.getValue());
						// zrx
						if (Asserts.isNull(record)) {
							record = new SqlExplainResult();
						}
						record.setParseTrue(true);
						record.setExplainTrue(true);
					} catch (Exception e) {
						String error = LogUtil.getError(e);
						record.setError(error);
						record.setParseTrue(false);
						record.setExplainTrue(false);
						correct = false;
						process.error(error);
					} finally {
						record.setType("Modify DML");
						record.setExplainTime(LocalDateTime.now());
						record.setSql(item.getValue());
						record.setIndex(index++);
						sqlExplainRecords.add(record);
					}
				}
			}
		}
		for (StatementParam item : jobParam.getExecute()) {
			SqlExplainResult record = new SqlExplainResult();
			try {
				record = executor.explainSqlRecord(item.getValue());
				if (Asserts.isNull(record)) {
					record = new SqlExplainResult();
				} else {
					executor.executeSql(item.getValue());
				}
				record.setType("DATASTREAM");
				record.setParseTrue(true);
			} catch (Exception e) {
				String error = LogUtil.getError(e);
				record.setError(error);
				record.setExplainTrue(false);
				record.setExplainTime(LocalDateTime.now());
				record.setSql(item.getValue());
				record.setIndex(index);
				sqlExplainRecords.add(record);
				correct = false;
				process.error(error);
				break;
			}
			record.setExplainTrue(true);
			record.setExplainTime(LocalDateTime.now());
			record.setSql(item.getValue());
			record.setIndex(index++);
			sqlExplainRecords.add(record);
		}
		process.info(StrUtil.format("A total of {} FlinkSQL have been Explained.", sqlExplainRecords.size()));
		return new ExplainResult(correct, sqlExplainRecords.size(), sqlExplainRecords);
	}

	public ObjectNode getStreamGraph(String statement) {
		JobParam jobParam = pretreatStatements(SqlUtil.getStatements(statement, sqlSeparator));
		if (jobParam.getDdl().size() > 0) {
			for (StatementParam statementParam : jobParam.getDdl()) {
				executor.executeSql(statementParam.getValue());
			}
		}
		if (jobParam.getTrans().size() > 0) {
			return executor.getStreamGraph(jobParam.getTransStatement());
		} else if (jobParam.getExecute().size() > 0) {
			List<String> datastreamPlans = new ArrayList<>();
			for (StatementParam item : jobParam.getExecute()) {
				datastreamPlans.add(item.getValue());
			}
			return executor.getStreamGraphFromDataStream(datastreamPlans);
		} else {
			return mapper.createObjectNode();
		}
	}

	public JobPlanInfo getJobPlanInfo(String statement) {
		JobParam jobParam = pretreatStatements(SqlUtil.getStatements(statement, sqlSeparator));
		if (jobParam.getDdl().size() > 0) {
			for (StatementParam statementParam : jobParam.getDdl()) {
				executor.executeSql(statementParam.getValue());
			}
		}
		if (jobParam.getTrans().size() > 0) {
			return executor.getJobPlanInfo(jobParam.getTransStatement());
		} else if (jobParam.getExecute().size() > 0) {
			List<String> datastreamPlans = new ArrayList<>();
			for (StatementParam item : jobParam.getExecute()) {
				datastreamPlans.add(item.getValue());
			}
			return executor.getJobPlanInfoFromDataStream(datastreamPlans);
		} else {
			throw new RuntimeException("Creating job plan fails because this job doesn't contain an insert statement.");
		}
	}

	private ObjectNode translateObjectNode(String statement) {
		return executor.getStreamGraph(statement);
	}

	private ObjectNode translateObjectNode(List<String> statement) {
		return executor.getStreamGraph(statement);
	}

	public List<LineageRel> getLineage(String statement) {
		JobConfig jobConfig =
				new JobConfig(
						"local",
						false,
						false,
						true,
						useStatementSet,
						1,
						executor.getTableConfig().getConfiguration().toMap());
		JobManager jm = JobManager.buildPlanMode(jobConfig);
		this.initialize(jm, jobConfig, statement);
		String[] sqls = SqlUtil.getStatements(statement, sqlSeparator);
		List<LineageRel> lineageRelList = new ArrayList<>();
		for (String item : sqls) {
			String sql = "";
			try {
				sql = FlinkInterceptor.pretreatStatement(executor, item, sqlSeparator);
				if (Asserts.isNullString(sql)) {
					continue;
				}
				SqlType operationType = Operations.getOperationType(sql);
				if (operationType.equals(SqlType.INSERT)) {
					lineageRelList.addAll(executor.getLineage(sql));
				} else if (!operationType.equals(SqlType.SELECT)) {
					executor.executeSql(sql);
				}
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		return lineageRelList;
	}
}
