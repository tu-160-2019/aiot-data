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

import lombok.Getter;
import lombok.Setter;
import net.srt.flink.common.result.IResult;
import net.srt.flink.executor.executor.Executor;
import net.srt.flink.executor.executor.ExecutorSetting;
import net.srt.flink.gateway.GatewayType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Job
 *
 * @author zrx
 * @since 2021/6/26 23:39
 */
@Getter
@Setter
public class Job {
	//历史id
	private Integer id;
	private Integer jobInstanceId;
	private JobConfig jobConfig;
	private String jobManagerAddress;
	private JobStatus status;
	private GatewayType type;
	private String statement;
	private String jobId;
	private String error;
	private IResult result;
	private ExecutorSetting executorSetting;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private Executor executor;
	private boolean useGateway;
	private List<String> jids;
	private boolean endByNoInsert;
	private String executeNo;
	private String executeSql;
	/**
	 * 节点调度的日志id
	 */
	private Integer nodeRecordId;

	public enum JobStatus {
		INITIALIZE,
		RUNNING,
		SUCCESS,
		FAILED,
		CANCEL
	}

	public Job(JobConfig jobConfig, GatewayType type, JobStatus status, String statement, ExecutorSetting executorSetting, Executor executor, boolean useGateway) {
		this.jobConfig = jobConfig;
		this.type = type;
		this.status = status;
		this.statement = statement;
		this.executorSetting = executorSetting;
		this.startTime = LocalDateTime.now();
		this.executor = executor;
		this.useGateway = useGateway;
	}

	public static Job init(GatewayType type, JobConfig jobConfig, ExecutorSetting executorSetting, Executor executor, String statement, boolean useGateway) {
		Job job = new Job(jobConfig, type, JobStatus.INITIALIZE, statement, executorSetting, executor, useGateway);
		job.setExecuteNo(UUID.randomUUID().toString().replaceAll("-", ""));
		return job;
	}

	public JobResult getJobResult() {
		return new JobResult(id, jobInstanceId, jobConfig, jobManagerAddress, status, statement, jobId, error, result, startTime, endTime);
	}

	public boolean isFailed() {
		return status.equals(JobStatus.FAILED);
	}
}
