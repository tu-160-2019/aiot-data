package net.srt.quartz.task.master.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.srt.api.module.data.governance.dto.DataGovernanceMasterDistributeDto;
import net.srt.api.module.data.governance.dto.DataGovernanceMasterModelDto;
import net.srt.api.module.data.governance.dto.distribute.DistributeDb;
import net.srt.api.module.data.integrate.constant.CommonRunStatus;
import net.srt.api.module.data.integrate.dto.DataDatabaseDto;
import net.srt.quartz.task.master.AbstractMasterAdapter;
import org.springframework.util.Assert;
import srt.cloud.framework.dbswitch.common.constant.MapperType;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.type.SourceType;
import srt.cloud.framework.dbswitch.data.config.DbswichProperties;
import srt.cloud.framework.dbswitch.data.domain.DbSwitchResult;
import srt.cloud.framework.dbswitch.data.entity.SourceDataSourceProperties;
import srt.cloud.framework.dbswitch.data.entity.TargetDataSourceProperties;
import srt.cloud.framework.dbswitch.data.service.MigrationService;
import srt.cloud.framework.dbswitch.data.util.BytesUnitUtils;

import java.util.Date;

/**
 * @ClassName DistributeDbImpl
 * @Author zrx
 */
@Slf4j
public class DistributeDbImpl extends AbstractMasterAdapter {

	public DistributeDbImpl(DataGovernanceMasterDistributeDto distributeDto) {
		super(distributeDto);
	}

	@SneakyThrows
	@Override
	public void distribute() {
		DistributeDb distributeDb = distributeDto.getDistributeJson().getDistributeDb();
		DbswichProperties dbswichProperties = new DbswichProperties();
		//源端为主数据
		SourceDataSourceProperties sourceDataSourceProperties = new SourceDataSourceProperties();
		sourceDataSourceProperties.setSourceType(SourceType.TABLE.getCode());
		dbswichProperties.getSource().add(sourceDataSourceProperties);
		ProductTypeEnum sourceProduct = ProductTypeEnum.getByIndex(project.getDbType());
		sourceDataSourceProperties.setSourceProductType(sourceProduct);
		sourceDataSourceProperties.setUrl(project.getDbUrl());
		sourceDataSourceProperties.setDriverClassName(sourceProduct.getDriveClassName());
		sourceDataSourceProperties.setUsername(project.getDbUsername());
		sourceDataSourceProperties.setPassword(project.getDbPassword());
		sourceDataSourceProperties.setFetchSize(distributeDb.getFetchSize());
		sourceDataSourceProperties.setSourceSchema(project.getDbSchema());
		sourceDataSourceProperties.setIncludeOrExclude(1);
		DataGovernanceMasterModelDto modelDto = dataMasterApi.getMasterModelById(distributeDto.getMasterModelId()).getData();
		sourceDataSourceProperties.setSourceIncludes(modelDto.getTableName());
		sourceDataSourceProperties.setRegexTableMapper(distributeDb.getRegexTableMapper());
		sourceDataSourceProperties.setRegexColumnMapper(distributeDb.getRegexColumnMapper());
		sourceDataSourceProperties.setMapperType(MapperType.ALL.name());
		//目的端
		TargetDataSourceProperties targetDataSourceProperties = dbswichProperties.getTarget();
		//获取数据库信息
		DataDatabaseDto databaseDto = databaseApi.getById(distributeDb.getDatabaseId()).getData();
		Assert.notNull(databaseDto, "派发数据库查询失败，可能已被删除！");
		ProductTypeEnum targetProduct = ProductTypeEnum.getByIndex(databaseDto.getDatabaseType());
		targetDataSourceProperties.setTargetProductType(targetProduct);
		targetDataSourceProperties.setUrl(databaseDto.getJdbcUrl());
		targetDataSourceProperties.setDriverClassName(targetProduct.getDriveClassName());
		targetDataSourceProperties.setUsername(databaseDto.getUserName());
		targetDataSourceProperties.setPassword(databaseDto.getPassword());
		targetDataSourceProperties.setTargetSchema(databaseDto.getDatabaseSchema());
		targetDataSourceProperties.setTargetDrop(distributeDb.getTargetDrop());
		targetDataSourceProperties.setSyncExist(distributeDb.getSyncExist());
		targetDataSourceProperties.setOnlyCreate(distributeDb.getOnlyCreate());
		targetDataSourceProperties.setLowercase(distributeDb.getLowercase());
		targetDataSourceProperties.setUppercase(distributeDb.getUppercase());
		targetDataSourceProperties.setChangeDataSync(distributeDb.getChangeDataSync());

		//构建service
		MigrationService service = new MigrationService(dbswichProperties, tableResult -> {
			distributeLogDto.setErrorInfo(tableResult.getErrorMsg());
		});
		DbSwitchResult result = service.run();
		distributeLogDto.setEndTime(new Date());
		distributeLogDto.setRunStatus(result.getIfAllSuccess().get() ? CommonRunStatus.SUCCESS.getCode() : CommonRunStatus.FAILED.getCode());
		distributeLogDto.setDataCount(result.getTotalRowCount().get());
		distributeLogDto.setByteCount(BytesUnitUtils.bytesSizeToHuman(result.getTotalBytes().get()));
		dataMasterApi.upDistributeLog(distributeLogDto);
	}
}
