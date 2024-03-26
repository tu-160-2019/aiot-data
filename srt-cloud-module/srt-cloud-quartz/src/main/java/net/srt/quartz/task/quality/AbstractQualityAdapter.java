// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package net.srt.quartz.task.quality;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import net.srt.api.module.data.governance.DataQualityApi;
import net.srt.api.module.data.governance.dto.DataGovernanceQualityTaskColumnDto;
import net.srt.api.module.data.governance.dto.DataGovernanceQualityTaskDto;
import net.srt.api.module.data.governance.dto.DataGovernanceQualityTaskTableDto;
import net.srt.api.module.data.governance.dto.quality.QualityCheck;
import net.srt.api.module.data.governance.dto.quality.QulaityColumn;
import net.srt.api.module.data.integrate.constant.CommonRunStatus;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.util.DbswitchStrUtils;
import srt.cloud.framework.dbswitch.common.util.SingletonObject;
import srt.cloud.framework.dbswitch.common.util.TypeConvertUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
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
public abstract class AbstractQualityAdapter {

	protected QualityCheck qualityCheck;
	protected DataQualityApi dataQualityApi;
	protected DataGovernanceQualityTaskDto qualityTaskDto;
	protected String regx;

	public AbstractQualityAdapter(QualityCheck qualityCheck) {
		this.qualityCheck = qualityCheck;
	}

	public void prepare(DataQualityApi dataQualityApi, DataGovernanceQualityTaskDto qualityTaskDto) {
		this.dataQualityApi = dataQualityApi;
		this.qualityTaskDto = qualityTaskDto;
	}

	public void setRegx(String regx) {
		this.regx = regx;
	}

	public static HikariDataSource createDataSource(QualityCheck qualityCheck) {
		ProductTypeEnum productTypeEnum = ProductTypeEnum.getByIndex(qualityCheck.getDatabaseType());
		HikariDataSource ds = new HikariDataSource();
		ds.setJdbcUrl(qualityCheck.getJdbcUrl());
		ds.setDriverClassName(productTypeEnum.getDriveClassName());
		ds.setUsername(qualityCheck.getUserName());
		ds.setPassword(qualityCheck.getPassword());
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

	protected DataGovernanceQualityTaskTableDto addTaskTable(List<QulaityColumn> columns) {
		DataGovernanceQualityTaskTableDto qualityTaskTableDto = new DataGovernanceQualityTaskTableDto();
		qualityTaskTableDto.setQualityTaskId(qualityTaskDto.getId());
		qualityTaskTableDto.setTableMetadataId(qualityCheck.getTableMetadataId());
		qualityTaskTableDto.setTableName(qualityCheck.getTableName());
		qualityTaskTableDto.setStatus(CommonRunStatus.RUNNING.getCode());
		qualityTaskTableDto.setColumnInfo(columns);
		qualityTaskTableDto.setProjectId(qualityTaskDto.getProjectId());
		qualityTaskTableDto.setCheckTime(new Date());
		qualityTaskTableDto.setStartTime(new Date());
		qualityTaskTableDto = dataQualityApi.addTaskTable(qualityTaskTableDto).getData();
		return qualityTaskTableDto;
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

	protected DataGovernanceQualityTaskColumnDto buildTaskColumn(DataGovernanceQualityTaskTableDto taskTable, Map<String, Object> map, boolean pass, String notPassInfo) throws JsonProcessingException {
		DataGovernanceQualityTaskColumnDto columnDto = new DataGovernanceQualityTaskColumnDto();
		columnDto.setQualityTaskId(taskTable.getQualityTaskId());
		columnDto.setQualityTaskTableId(taskTable.getId());
		columnDto.setCheckRow(SingletonObject.OBJECT_MAPPER.writeValueAsString(map));
		columnDto.setCheckTime(new Date());
		columnDto.setProjectId(taskTable.getProjectId());
		qualityTaskDto.setCheckCount(qualityTaskDto.getCheckCount() + 1);
		taskTable.setCheckCount(taskTable.getCheckCount() + 1);
		if (pass) {
			qualityTaskDto.setPassCount(qualityTaskDto.getPassCount() + 1);
			taskTable.setPassCount(taskTable.getPassCount() + 1);
			columnDto.setCheckResult(1);
		} else {
			qualityTaskDto.setNotPassCount(qualityTaskDto.getNotPassCount() + 1);
			columnDto.setNotPassInfo(notPassInfo);
			taskTable.setNotPassCount(taskTable.getNotPassCount() + 1);
			columnDto.setCheckResult(0);
		}
		return columnDto;
	}

	protected void updateTask(DataGovernanceQualityTaskTableDto taskTable, List<DataGovernanceQualityTaskColumnDto> columnDtos) {
		dataQualityApi.updateQualityTask(qualityTaskDto);
		dataQualityApi.updateQualityTaskTable(taskTable);
		//添加字段检测记录
		dataQualityApi.addQualityTaskColumns(columnDtos);
		columnDtos.clear();
	}

	public abstract void check();
}
