// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbwriter.mongodb;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.util.SingletonObject;
import srt.cloud.framework.dbswitch.dbwriter.AbstractDatabaseWriter;
import srt.cloud.framework.dbswitch.dbwriter.IDatabaseWriter;
import srt.cloud.framework.dbswitch.dbwriter.util.ObjectCastUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * DB2数据库写入实现类
 *
 * @author jrl
 */
@Slf4j
public class MongoWriterImpl extends AbstractDatabaseWriter implements IDatabaseWriter {

	public MongoWriterImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	protected String getDatabaseProductName() {
		return "MongoDB";
	}

	@Override
	public void prepareWrite(String schemaName, String tableName, List<String> fieldNames) {
		this.columnType = Collections.emptyMap();
		this.schemaName = schemaName;
		this.tableName = tableName;

		try (Connection connection = getDataSource().getConnection()) {
			try (Statement stmt = connection.createStatement()) {
				stmt.executeUpdate(String.format("%s.getCollection('%s').drop();", schemaName, tableName));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public long write(List<String> fieldNames, List<Object[]> recordValues) {
		if (CollectionUtils.isEmpty(fieldNames) || CollectionUtils.isEmpty(recordValues)) {
			return 0L;
		}
		for (List<Object[]> partRecordValues : Lists.partition(recordValues, 500)) {
			String sql = String.format("%s.getCollection('%s').insertMany", schemaName, tableName) +
					"( " + asString(fieldNames, partRecordValues) + " )";
			try (Connection connection = getDataSource().getConnection()) {
				try (Statement stmt = connection.createStatement()) {
					stmt.executeUpdate(sql);
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		return recordValues.size();
	}

	@SneakyThrows
	private String asString(List<String> fieldNames, List<Object[]> recordValues) {
		int fieldCount = Math.min(fieldNames.size(), recordValues.get(0).length);
		List<Map<String, Object>> rows = new ArrayList<>(recordValues.size());
		for (Object[] row : recordValues) {
			Map<String, Object> columns = new LinkedHashMap<>(fieldCount);
			for (int i = 0; i < fieldCount; ++i) {
				columns.put(fieldNames.get(i), row[i]);
			}
			rows.add(columns);
		}
		return SingletonObject.OBJECT_MAPPER.writeValueAsString(rows);
	}

}
