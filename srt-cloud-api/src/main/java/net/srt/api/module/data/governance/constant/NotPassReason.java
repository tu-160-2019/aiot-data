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
public enum NotPassReason {

	/**
	 * 空值
	 */
	NULL(1, "空值"),
	/**
	 * 类型不匹配
	 */
	TYPE_NOT_FORMAT(2, "类型不匹配"),

	/**
	 * 长度有误
	 */
	LENGTH_ERROR(3, "长度有误"),

	/**
	 * 格式错误
	 */
	FORMAT_ERROR(4, "格式错误"),


	/**
	 * 格式错误
	 */
	NOT_UNIQUE(5, "不唯一");


	private final Integer value;
	private final String longValue;

	NotPassReason(Integer value, String longValue) {
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
