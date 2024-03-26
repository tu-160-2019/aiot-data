// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package srt.cloud.framework.dbswitch.data.util;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.data.entity.SourceDataSourceProperties;
import srt.cloud.framework.dbswitch.data.entity.TargetDataSourceProperties;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Objects;

/**
 * DataSource工具类
 *
 * @author jrl
 */
@Slf4j
public final class DataSourceUtils {

	/**
	 * 创建于指定数据库连接描述符的连接池
	 *
	 * @param properties 数据库连接描述符
	 * @return HikariDataSource连接池
	 */
	public static HikariDataSource createSourceDataSource(SourceDataSourceProperties properties) {
		HikariDataSource ds = new HikariDataSource();
		//设置可以获取注释信息
		ds.addDataSourceProperty("remarksReporting", true);
		ds.addDataSourceProperty("useInformationSchema", true);
		ds.setPoolName("The_Source_DB_Connection");
		ds.setJdbcUrl(properties.getUrl());
		ds.setDriverClassName(properties.getDriverClassName());
		ds.setUsername(properties.getUsername());
		ds.setPassword(properties.getPassword());
		if (properties.getDriverClassName().contains("oracle")) {
			System.getProperties().setProperty("oracle.jdbc.J2EE13Compliant", "true");
		}
		ProductTypeEnum sourceProductType = properties.getSourceProductType();
		ds.setConnectionTestQuery(sourceProductType.getTestSql());
		ds.setMaximumPoolSize(properties.getPoolSize());
		ds.setMinimumIdle(5);
		ds.setMaxLifetime(properties.getMaxLifeTime());
		ds.setConnectionTimeout(properties.getConnectionTimeout());
		ds.setIdleTimeout(60000);

		return ds;
	}

	/**
	 * 创建于指定数据库连接描述符的连接池
	 *
	 * @param properties 数据库连接描述符
	 * @return HikariDataSource连接池
	 */
	public static HikariDataSource createTargetDataSource(TargetDataSourceProperties properties) {
		HikariDataSource ds = new HikariDataSource();
		ds.setPoolName("The_Target_DB_Connection");
		ds.setJdbcUrl(properties.getUrl());
		ds.setDriverClassName(properties.getDriverClassName());
		ds.setUsername(properties.getUsername());
		ds.setPassword(properties.getPassword());
		ProductTypeEnum targetProductType = properties.getTargetProductType();
		ds.setConnectionTestQuery(targetProductType.getTestSql());
		ds.setMaximumPoolSize(properties.getPoolSize());
		ds.setMinimumIdle(5);
		ds.setMaxLifetime(properties.getMaxLifeTime());
		ds.setConnectionTimeout(properties.getConnectionTimeout());
		ds.setIdleTimeout(60000);

		// 如果是Greenplum数据库，这里需要关闭会话的查询优化器
		if (properties.getDriverClassName().contains("postgresql")) {
			org.springframework.jdbc.datasource.DriverManagerDataSource dataSource = new org.springframework.jdbc.datasource.DriverManagerDataSource();
			dataSource.setDriverClassName(properties.getDriverClassName());
			dataSource.setUrl(properties.getUrl());
			dataSource.setUsername(properties.getUsername());
			dataSource.setPassword(properties.getPassword());
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			String versionString = jdbcTemplate.queryForObject("SELECT version()", String.class);
			if (Objects.nonNull(versionString) && versionString.contains("Greenplum")) {
				log.info(
						"#### Target database is Greenplum Cluster, Close Optimizer now: set optimizer to 'off' ");
				ds.setConnectionInitSql("set optimizer to 'off'");
			}
		}

		return ds;
	}

	private DataSourceUtils() {
	}

}
