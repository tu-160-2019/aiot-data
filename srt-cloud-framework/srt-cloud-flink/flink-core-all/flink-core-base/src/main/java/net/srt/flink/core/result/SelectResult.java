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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.srt.flink.common.result.AbstractResult;
import net.srt.flink.common.result.IResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SelectResult
 *
 * @author zrx
 * @since 2021/5/25 16:01
 **/
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SelectResult extends AbstractResult implements IResult {

    private String jobId;
    private List<Map<String, Object>> rowData;
    private Integer total;
    private Integer currentCount;
    private LinkedHashSet<String> columns;
    private boolean isDestroyed;
    private boolean end = false;

    public SelectResult(List<Map<String, Object>> rowData, Integer total, Integer currentCount, LinkedHashSet<String> columns,
						String jobId, boolean success) {
        this.rowData = rowData;
        this.total = total;
        this.currentCount = currentCount;
        this.columns = columns;
        this.jobId = jobId;
        this.success = success;
        //this.endTime = LocalDateTime.now();
        this.isDestroyed = false;
    }

    public SelectResult(String jobId, List<Map<String, Object>> rowData, LinkedHashSet<String> columns) {
        this.jobId = jobId;
        this.rowData = rowData;
        this.total = rowData.size();
        this.columns = columns;
        this.success = true;
        this.isDestroyed = false;
    }

    public SelectResult(String jobId, boolean isDestroyed, boolean success) {
        this.jobId = jobId;
        this.isDestroyed = isDestroyed;
        this.success = success;
        this.endTime = LocalDateTime.now();
    }

    @Override
    public String getJobId() {
        return jobId;
    }

    public static SelectResult buildDestruction(String jobID) {
        return new SelectResult(jobID, true, false);
    }

    public static SelectResult buildSuccess(String jobID) {
		SelectResult selectResult = new SelectResult(jobID, false, true);
		selectResult.setRowData(new ArrayList<>());
		selectResult.setColumns(new LinkedHashSet<>());
		return selectResult;
	}

    public static SelectResult buildFailed() {
        return new SelectResult(null, false, false);
    }
}
