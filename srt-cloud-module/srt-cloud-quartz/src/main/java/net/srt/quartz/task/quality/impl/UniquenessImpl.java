package net.srt.quartz.task.quality.impl;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import net.srt.api.module.data.governance.constant.NotPassReason;
import net.srt.api.module.data.governance.dto.DataGovernanceQualityTaskColumnDto;
import net.srt.api.module.data.governance.dto.DataGovernanceQualityTaskTableDto;
import net.srt.api.module.data.governance.dto.quality.QualityCheck;
import net.srt.api.module.data.governance.dto.quality.QualityConfigParam;
import net.srt.api.module.data.governance.dto.quality.QulaityColumn;
import net.srt.api.module.data.integrate.constant.CommonRunStatus;
import net.srt.flink.common.utils.LogUtil;
import net.srt.quartz.task.quality.AbstractQualityAdapter;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.core.service.IMetaDataByJdbcService;
import srt.cloud.framework.dbswitch.core.service.impl.MetaDataByJdbcServiceImpl;
import srt.cloud.framework.dbswitch.dbcommon.database.DatabaseOperatorFactory;
import srt.cloud.framework.dbswitch.dbcommon.database.IDatabaseOperator;
import srt.cloud.framework.dbswitch.dbcommon.domain.StatementResultSet;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName UniquenessImpl
 * @Author zrx
 * @Date 2023/6/24 13:13
 */
@Slf4j
public class UniquenessImpl extends AbstractQualityAdapter {

	private final static Integer BATCH_SIZE = 5000;

	public UniquenessImpl(QualityCheck qualityCheck) {
		super(qualityCheck);
	}

	@Override
	public void check() {
		log.info(String.format("UniquenessImpl start check,jdbcUrl:%s,tableName:%s", qualityCheck.getJdbcUrl(), qualityCheck.getTableName()));
		//连接数据库
		ProductTypeEnum productTypeEnum = ProductTypeEnum.getByIndex(qualityCheck.getDatabaseType());
		List<QulaityColumn> qulaityColumns = qualityCheck.getQulaityColumns();
		//判断是组合式唯一还是单字段唯一
		QualityConfigParam param = qualityCheck.getParam();
		IMetaDataByJdbcService metaDataService = new MetaDataByJdbcServiceImpl(productTypeEnum);

		if (param.getUniqueType() == 1) {
			//单字段唯一，每个字段检测一次
			for (QulaityColumn qulaityColumn : qulaityColumns) {
				List<QulaityColumn> columnInfos = new ArrayList<>();
				columnInfos.add(qulaityColumn);
				addTaskColumns(productTypeEnum, metaDataService, columnInfos);
			}
		} else {
			//组合字段唯一
			addTaskColumns(productTypeEnum, metaDataService, qulaityColumns);
		}
		log.info(String.format("UniquenessImpl check end,jdbcUrl:%s,tableName:%s", qualityCheck.getJdbcUrl(), qualityCheck.getTableName()));

	}

	private void addTaskColumns(ProductTypeEnum productTypeEnum, IMetaDataByJdbcService metaDataService, List<QulaityColumn> columnInfos) {
		DataGovernanceQualityTaskTableDto taskTable = addTaskTable(columnInfos);
		try {
			List<String> columns = columnInfos.stream().map(QulaityColumn::getColumnName).collect(Collectors.toList());
			String countMoreThanOneSql = metaDataService.getCountMoreThanOneSql(qualityCheck.getDatabaseSchema(), qualityCheck.getTableName(), columns);
			String countOneSql = metaDataService.getCountOneSql(qualityCheck.getDatabaseSchema(), qualityCheck.getTableName(), columns);
			//挨个字段检测唯一
			executeSql(productTypeEnum, countMoreThanOneSql, columns, taskTable, false);
			executeSql(productTypeEnum, countOneSql, columns, taskTable, true);
			taskTable.setEndTime(new Date());
			taskTable.setStatus(CommonRunStatus.SUCCESS.getCode());
			dataQualityApi.updateQualityTaskTable(taskTable);
		} catch (Exception e) {
			taskTable.setEndTime(new Date());
			taskTable.setErrorLog(LogUtil.getError(e));
			taskTable.setStatus(CommonRunStatus.FAILED.getCode());
			dataQualityApi.updateQualityTaskTable(taskTable);
			throw new RuntimeException(e);
		}
	}

	private void executeSql(ProductTypeEnum productTypeEnum, String sql, List<String> columns, DataGovernanceQualityTaskTableDto taskTable, Boolean pass) {
		try (HikariDataSource dataSource = createDataSource(qualityCheck)) {
			IDatabaseOperator sourceOperator = DatabaseOperatorFactory
					.createDatabaseOperator(dataSource, productTypeEnum);
			sourceOperator.setFetchSize(BATCH_SIZE);
			try (StatementResultSet srs = sourceOperator.queryTableData(sql); ResultSet rs = srs.getResultset()) {
				int size = 0;
				List<DataGovernanceQualityTaskColumnDto> columnDtos = new ArrayList<>();
				while (rs.next()) {
					size++;
					Map<String, Object> map = buildRowMap(columns, rs);
					DataGovernanceQualityTaskColumnDto columnDto = buildTaskColumn(taskTable, map, pass, String.join(",", columns) + "不唯一");
					if (!pass) {
						columnDto.setNotPassReason(NotPassReason.NOT_UNIQUE.getValue());
					}
					columnDtos.add(columnDto);
					//5000一次
					if (size % BATCH_SIZE == 0) {
						updateTask(taskTable, columnDtos);
					}
				}
				// 检查剩下没更新的
				if (!columnDtos.isEmpty()) {
					updateTask(taskTable, columnDtos);
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

}
