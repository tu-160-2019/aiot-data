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

package net.srt.flink.common.config;


import net.srt.flink.common.assertion.Asserts;

/**
 * Dialect
 *
 * @author zrx
 * @since 2021/12/13
 **/
public enum Dialect {

	SQL(1, "Sql"),
	FLINKSQL(2, "FlinkSql"),
	FLINKJAR(3, "FlinkJar"),
	FLINKSQLENV(4, "FlinkSqlEnv"),
	JAVA(5, "Java"),
	PYTHON(6, "Python"),
	SCALA(7, "Scala"),
	MYSQL(8, "Mysql"),
	ORACLE(9, "Oracle"),
	SQLSERVER(10, "SqlServer"),
	POSTGRESQL(11, "PostgreSql"),
	CLICKHOUSE(12, "ClickHouse"),
	DORIS(13, "Doris"),
	PHOENIX(14, "Phoenix"),
	HIVE(15, "Hive"),
	STARROCKS(16, "StarRocks"),
	PRESTO(17, "Presto"),
	KUBERNETES_APPLICATION(18, "KubernetesApplaction");

	private Integer code;
	private String value;

	public static final Dialect DEFAULT = Dialect.FLINKSQL;

	Dialect(Integer code, String value) {
		this.code = code;
		this.value = value;
	}

	public Integer getCode() {
		return code;
	}

	public String getValue() {
		return value;
	}

	public boolean equalsVal(String valueText) {
		return Asserts.isEqualsIgnoreCase(value, valueText);
	}

	public static Dialect get(String value) {
		for (Dialect type : Dialect.values()) {
			if (Asserts.isEqualsIgnoreCase(type.getValue(), value)) {
				return type;
			}
		}
		return Dialect.FLINKSQL;
	}

	public static Dialect getByCode(String code) {
		for (Dialect type : Dialect.values()) {
			if (type.getCode().toString().equals(code)) {
				return type;
			}
		}
		return Dialect.FLINKSQL;
	}

	/**
	 * Judge sql dialect.
	 *
	 * @param value {@link Dialect}
	 * @return If is flink sql, return false, otherwise return true.
	 */
	public static boolean notFlinkSql(String value) {
		Dialect dialect = Dialect.get(value);
		switch (dialect) {
			case SQL:
			case MYSQL:
			case ORACLE:
			case SQLSERVER:
			case POSTGRESQL:
			case CLICKHOUSE:
			case DORIS:
			case PHOENIX:
			case HIVE:
			case STARROCKS:
			case PRESTO:
				return true;
			default:
				return false;
		}
	}

	public static boolean isUDF(String value) {
		Dialect dialect = Dialect.get(value);
		switch (dialect) {
			case JAVA:
			case SCALA:
			case PYTHON:
				return true;
			default:
				return false;
		}
	}
}
