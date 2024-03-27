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

package net.srt.flink.core.result;

import com.google.common.collect.Streams;
import lombok.extern.slf4j.Slf4j;
import net.srt.flink.client.utils.FlinkUtil;
import net.srt.flink.common.utils.LogUtil;
import net.srt.flink.executor.constant.FlinkConstant;
import org.apache.flink.core.execution.JobClient;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.types.Row;
import org.apache.flink.types.RowKind;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * ResultRunnable
 *
 * @author zrx
 * @since 2021/7/1 22:50
 */
@Slf4j
public class ResultRunnable implements Runnable {

	private String jobId;
	private static final String nullColumn = "";
	private final TableResult tableResult;
	private final Integer maxRowNum;
	private final boolean isChangeLog;
	private final boolean isAutoCancel;
	private final String timeZone;

	public ResultRunnable(String jobId, TableResult tableResult, Integer maxRowNum, boolean isChangeLog, boolean isAutoCancel,
						  String timeZone) {
		this.jobId = jobId;
		this.tableResult = tableResult;
		this.maxRowNum = maxRowNum;
		this.isChangeLog = isChangeLog;
		this.isAutoCancel = isAutoCancel;
		this.timeZone = timeZone;
	}

	@Override
	public void run() {
		try {
			getResult();
		} catch (Exception e) {
			// Nothing to do
		}
	}

	public void syncRun() {
		try {
			getResult();
		} catch (Exception e) {
			// Nothing to do
		}
	}

	private void getResult() {
		if (tableResult.getJobClient().isPresent()) {
			if (!ResultPool.containsKey(jobId)) {
				ResultPool.put(new SelectResult(jobId, new ArrayList<>(), new LinkedHashSet<>()));
			}
			SelectResult selectResult = ResultPool.get(jobId);
			try {
				// zrx 睡5s后获取，避免获取不到，比如非statement下前一条语句为insert紧跟着select，这个时候select不会有任何数据
				//Thread.sleep(5000);
				if (isChangeLog) {
					catchChangLog(selectResult);
				} else {
					catchData(selectResult);
				}
				selectResult.setEnd(true);
			} catch (Exception e) {
				selectResult.setSuccess(false);
				selectResult.setError(LogUtil.getError(e));
				selectResult.setEnd(true);
				log.error(e.getMessage(), e);
			}
		} else {
			SelectResult selectResult = ResultPool.get(jobId);
			selectResult.setSuccess(false);
			selectResult.setError("Flink JobClient is not present");
			selectResult.setEnd(true);
		}
	}

	private void catchChangLog(SelectResult selectResult) {
		List<Map<String, Object>> rows = selectResult.getRowData();
		List<String> columns = FlinkUtil.catchColumn(tableResult);

		columns.add(0, FlinkConstant.OP);
		selectResult.setColumns(new LinkedHashSet<>(columns));
		Streams.stream(tableResult.collect()).limit(maxRowNum).forEach(row -> {
			Map<String, Object> map = getFieldMap(columns.subList(1, columns.size()), row);
			map.put(FlinkConstant.OP, row.getKind().name() + ":" + row.getKind().shortString());
			rows.add(map);
		});

		if (isAutoCancel) {
			tableResult.getJobClient().ifPresent(JobClient::cancel);
		}
	}

	private void catchData(SelectResult selectResult) {
		List<Map<String, Object>> rows = selectResult.getRowData();
		List<String> columns = FlinkUtil.catchColumn(tableResult);

		selectResult.setColumns(new LinkedHashSet<>(columns));
		Streams.stream(tableResult.collect()).limit(maxRowNum).forEach(row -> {
			Map<String, Object> map = getFieldMap(columns, row);
			if (RowKind.UPDATE_BEFORE == row.getKind() || RowKind.DELETE == row.getKind()) {
				rows.remove(map);
			} else {
				rows.add(map);
			}
		});

		if (isAutoCancel) {
			tableResult.getJobClient().ifPresent(JobClient::cancel);
		}
	}

	private Map<String, Object> getFieldMap(List<String> columns, Row row) {
		Map<String, Object> map = new LinkedHashMap<>();
		for (int i = 0; i < row.getArity(); ++i) {
			Object field = row.getField(i);
			String column = columns.get(i);
			if (field == null) {
				map.put(column, nullColumn);
			} else if (field instanceof Instant) {
				map.put(column, ((Instant) field).atZone(ZoneId.of(timeZone)).toLocalDateTime().toString());
			} else {
				map.put(column, field);
			}
		}
		return map;
	}
}
