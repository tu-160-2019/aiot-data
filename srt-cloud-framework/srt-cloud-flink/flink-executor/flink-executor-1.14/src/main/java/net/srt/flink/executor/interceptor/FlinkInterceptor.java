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

package net.srt.flink.executor.interceptor;

import net.srt.flink.common.assertion.Asserts;
import net.srt.flink.common.utils.SqlUtil;
import net.srt.flink.executor.executor.Executor;
import net.srt.flink.executor.trans.Operation;
import net.srt.flink.executor.trans.Operations;
import org.apache.flink.table.api.TableResult;

/**
 * FlinkInterceptor
 *
 * @author zrx
 * @since 2021/6/11 22:17
 */
public class FlinkInterceptor {
	private FlinkInterceptor() {
	}

	public static String pretreatStatement(Executor executor, String statement, String sqlSeparator) {
		statement = SqlUtil.removeNote(statement);
		if (executor.isUseSqlFragment()) {
			statement = executor.getSqlManager().parseVariable(statement, sqlSeparator);
		}
		return statement.trim();
	}

	// return false to continue with executeSql
	public static FlinkInterceptorResult build(Executor executor, String statement) {
		boolean noExecute = false;
		TableResult tableResult = null;
		Operation operation = Operations.buildOperation(statement);
		if (Asserts.isNotNull(operation)) {
			tableResult = operation.build(executor);
			noExecute = operation.noExecute();
		}
		return FlinkInterceptorResult.build(noExecute, tableResult);
	}
}
