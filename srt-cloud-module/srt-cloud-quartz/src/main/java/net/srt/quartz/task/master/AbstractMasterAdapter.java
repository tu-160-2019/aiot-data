// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package net.srt.quartz.task.master;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import net.srt.api.module.data.governance.DataMasterApi;
import net.srt.api.module.data.governance.dto.DataGovernanceMasterDistributeDto;
import net.srt.api.module.data.governance.dto.DataGovernanceMasterDistributeLogDto;
import net.srt.api.module.data.integrate.DataDatabaseApi;
import net.srt.framework.common.cache.bean.DataProjectCacheBean;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.util.DbswitchStrUtils;
import srt.cloud.framework.dbswitch.common.util.TypeConvertUtils;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 数据库写入抽象基类
 *
 * @author jrl
 */
@Slf4j
public abstract class AbstractMasterAdapter {

	protected DataGovernanceMasterDistributeDto distributeDto;
	protected DataMasterApi dataMasterApi;
	protected DataDatabaseApi databaseApi;
	protected DataGovernanceMasterDistributeLogDto distributeLogDto;
	protected Thread currentThread;
	protected DataProjectCacheBean project;

	public AbstractMasterAdapter(DataGovernanceMasterDistributeDto distributeDto) {
		this.distributeDto = distributeDto;
	}

	public void prepare(DataMasterApi dataMasterApi, DataDatabaseApi databaseApi, DataGovernanceMasterDistributeLogDto distributeLogDto, Thread currentThread) {
		this.dataMasterApi = dataMasterApi;
		this.databaseApi = databaseApi;
		this.distributeLogDto = distributeLogDto;
		this.currentThread = currentThread;
	}

	public void setProject(DataProjectCacheBean project) {
		this.project = project;
	}

	protected HikariDataSource createDataSource() {
		ProductTypeEnum productTypeEnum = ProductTypeEnum.getByIndex(project.getDbType());
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl(project.getDbUrl());
		ds.setDriverClassName(productTypeEnum.getDriveClassName());
		ds.setUsername(project.getDbUsername());
		ds.setPassword(project.getDbPassword());
		if (ds.getDriverClassName().contains("oracle")) {
			ds.setConnectionTestQuery("SELECT 'Hello' from DUAL");
			// https://blog.csdn.net/qq_20960159/article/details/78593936
			System.getProperties().setProperty("oracle.jdbc.J2EE13Compliant", "true");
		} else if (ds.getDriverClassName().contains("db2")) {
			ds.setConnectionTestQuery("SELECT 1 FROM SYSIBM.SYSDUMMY1");
		} else {
			ds.setConnectionTestQuery("SELECT 1");
		}
		ds.setMaximumPoolSize(8);
		ds.setMinimumIdle(5);
		ds.setMaxLifetime(TimeUnit.MINUTES.toMillis(60));
		ds.setConnectionTimeout(TimeUnit.MINUTES.toMillis(60));
		ds.setIdleTimeout(60000);

		return ds;
	}

	protected Map<String, Object> buildRowMap(List<String> columns, ResultSet rs) throws SQLException {
		Map<String, Object> map = new HashMap<>();
		//转换
		for (int i = 1; i <= columns.size(); i++) {
			Object value = rs.getObject(i);
			String key = columns.get(i - 1);
			if (value instanceof byte[]) {
				map.put(key, DbswitchStrUtils.toHexString((byte[]) value));
			} else if (value instanceof java.sql.Clob) {
				map.put(key, TypeConvertUtils.castToString(value));
			} else if (value instanceof java.sql.Blob) {
				map.put(key, DbswitchStrUtils.toHexString(TypeConvertUtils.castToByteArray(value)));
			} else {
				map.put(key, null == value ? null : value.toString());
			}
		}
		return map;
	}

	public abstract void distribute();



}
