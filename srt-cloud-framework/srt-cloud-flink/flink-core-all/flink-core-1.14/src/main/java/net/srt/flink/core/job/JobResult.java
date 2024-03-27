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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.srt.flink.common.result.IResult;

import java.time.LocalDateTime;

/**
 * JobResult
 *
 * @author zrx
 * @since 2021/6/29 23:56
 */
@Data
@AllArgsConstructor
public class JobResult {
	private Integer id;
	private JobConfig jobConfig;
	private String jobManagerAddress;
	private Job.JobStatus status;
	private boolean success;
	private String statement;
	private String jobId;
	private Integer jobInstanceId;
	private String error;
	//private IResult result;
	private Object result;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime startTime;
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endTime;

	private String log;

	public JobResult() {
	}

	public JobResult(Integer id, Integer jobInstanceId, JobConfig jobConfig, String jobManagerAddress, Job.JobStatus status,
					 String statement, String jobId, String error, IResult result, LocalDateTime startTime, LocalDateTime endTime) {
		this.id = id;
		this.jobInstanceId = jobInstanceId;
		this.jobConfig = jobConfig;
		this.jobManagerAddress = jobManagerAddress;
		this.status = status;
		this.success = status.equals(Job.JobStatus.SUCCESS);
		this.statement = statement;
		this.jobId = jobId;
		this.error = error;
		this.result = result;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public void setStartTimeNow() {
		this.setStartTime(LocalDateTime.now());
	}

	public void setEndTimeNow() {
		this.setEndTime(LocalDateTime.now());
	}
}
