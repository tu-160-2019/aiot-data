// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.dbwriter.mysql;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import srt.cloud.framework.dbswitch.dbwriter.AbstractDatabaseWriter;
import srt.cloud.framework.dbswitch.dbwriter.IDatabaseWriter;
import srt.cloud.framework.dbswitch.dbwriter.util.ObjectCastUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * MySQL数据库写入实现类
 *
 * @author jrl
 */
@Slf4j
public class MySqlWriterImpl extends AbstractDatabaseWriter implements IDatabaseWriter {

	/*private DefaultTransactionDefinition definition;*/

	public MySqlWriterImpl(DataSource dataSource) {
		super(dataSource);

		/*this.definition = new DefaultTransactionDefinition();
		this.definition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		this.definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		this.definition.setTimeout(3600);*/
	}

	@Override
	protected String getDatabaseProductName() {
		return "MySQL";
	}

	@Override
	protected String selectTableMetaDataSqlString(String schemaName, String tableName,
												  List<String> fieldNames) {
		if (CollectionUtils.isEmpty(fieldNames)) {
			return String.format("SELECT *  FROM `%s`.`%s`  WHERE 1=2", schemaName, tableName);
		} else {
			return String.format("SELECT `%s`  FROM `%s`.`%s`  WHERE 1=2",
					StringUtils.join(fieldNames, "`,`"), schemaName, tableName);
		}
	}

	@Override
	public long write(List<String> fieldNames, List<Object[]> recordValues) {
		if (recordValues.isEmpty()) {
			return 0;
		}

		recordValues.parallelStream().forEach((Object[] row) -> {
			for (int i = 0; i < row.length; ++i) {
				try {
					row[i] = ObjectCastUtils.castByDetermine(row[i]);
				} catch (Exception e) {
					row[i] = null;
				}
			}
		});

		/*List<String> placeHolders = Collections.nCopies(fieldNames.size(), "?");
		StringBuilder sqlInsert = new StringBuilder(String.format("INSERT INTO `%s`.`%s` ( `%s` ) VALUES ",
				schemaName, tableName,
				StringUtils.join(fieldNames, "`,`")));
		//mysql 一条语句插入多条数据可以提升近一倍速率 sql语句长度有限制，合并sql语句时要注意。长度限制可以通过max_allowed_packet配置项修改，默认为1M。
		for (int i = 0; i < recordValues.size(); i++) {
			sqlInsert.append(String.format("( %s )", StringUtils.join(placeHolders, ",")));
			if (i < recordValues.size() - 1) {
				sqlInsert.append(",");
			}
		}*/
		List<String> placeHolders = Collections.nCopies(fieldNames.size(), "?");
		String sqlInsert = String.format("INSERT INTO `%s`.`%s` ( `%s` ) VALUES ( %s )",
				schemaName, tableName,
				StringUtils.join(fieldNames, "`,`"),
				StringUtils.join(placeHolders, ","));

		int[] argTypes = new int[fieldNames.size()];
		for (int i = 0; i < fieldNames.size(); ++i) {
			String col = fieldNames.get(i);
			argTypes[i] = this.columnType.get(col);
		}

		batchWrite(fieldNames, recordValues, sqlInsert, argTypes);

		/*PlatformTransactionManager transactionManager = new DataSourceTransactionManager(
				this.dataSource);
		TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager,
				definition);
		Integer ret = transactionTemplate.execute((TransactionStatus transactionStatus) -> {
			try {
				long start = System.currentTimeMillis();
				int[] affects = jdbcTemplate.batchUpdate(sqlInsert, recordValues, argTypes);
				log.info("sync use time:" + (System.currentTimeMillis() - start));
				Integer affectCount = affects.length;
				if (log.isDebugEnabled()) {
					log.debug("{} insert data affect count: {}", getDatabaseProductName(), affectCount);
				}
				return affectCount;
			} catch (Throwable t) {
				transactionStatus.setRollbackOnly();
				throw t;
			}
		});*/

		int size = recordValues.size();
		if (log.isDebugEnabled()) {
			/*log.debug("MySQL insert write data  affect count:{}", ret.longValue());*/
			log.debug("MySQL insert write data  affect count:{}", size);
		}

		recordValues.clear();
		return size;
	}

	/*@Override
	public void batchWrite(List<String> fieldNames, List<Object[]> recordValues, String sqlInsert, int[] argTypes) {
		try (Connection connection = dataSource.getConnection();
			 PreparedStatement ps = connection.prepareStatement(sqlInsert);) {
			int i = 1;
			for (Object[] recordValue : recordValues) {
				for (int j = 0; j < fieldNames.size(); j++) {
					ps.setObject(i, recordValue[j], argTypes[j]);
					i++;
				}
			}
			ps.executeUpdate();
		} catch (SQLException se) {
			throw new RuntimeException(se);
		}
	}*/

}
