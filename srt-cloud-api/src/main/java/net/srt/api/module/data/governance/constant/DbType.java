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

package net.srt.api.module.data.governance.constant;


/**
 * FlinkType
 *
 * @author zrx
 **/
public enum DbType {

	/**
	 * 数据库
	 */
	DATABASE(1, "数据库"),
	/**
	 * 数据库
	 */
	MIDDLE_DB(2, "中台库");


	private final Integer value;
	private final String longValue;

	DbType(Integer value, String longValue) {
		this.value = value;
		this.longValue = longValue;
	}

	public Integer getValue() {
		return value;
	}

	public String getLongValue() {
		return longValue;
	}
}
