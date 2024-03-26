package net.srt.quartz.task.master.impl;

import com.zaxxer.hikari.HikariDataSource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.srt.api.module.data.governance.dto.DataGovernanceMasterColumnDto;
import net.srt.api.module.data.governance.dto.DataGovernanceMasterDistributeDto;
import net.srt.api.module.data.governance.dto.DataGovernanceMasterModelDto;
import net.srt.api.module.data.governance.dto.distribute.ApiParam;
import net.srt.api.module.data.governance.dto.distribute.DistributeApi;
import net.srt.api.module.data.integrate.constant.CommonRunStatus;
import net.srt.framework.common.exception.ServerException;
import net.srt.framework.common.utils.HttpResponse;
import net.srt.framework.common.utils.HttpUtil;
import net.srt.quartz.task.master.AbstractMasterAdapter;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.message.BasicHeader;
import org.ehcache.sizeof.SizeOf;
import org.springframework.util.CollectionUtils;
import srt.cloud.framework.dbswitch.common.type.ProductTypeEnum;
import srt.cloud.framework.dbswitch.common.util.SingletonObject;
import srt.cloud.framework.dbswitch.data.util.BytesUnitUtils;
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
 * @ClassName DistributeApiImpl
 * @Author zrx
 */
@Slf4j
public class DistributeApiImpl extends AbstractMasterAdapter {

	public DistributeApiImpl(DataGovernanceMasterDistributeDto distributeDto) {
		super(distributeDto);
	}

	@Override
	public void distribute() {
		try (HikariDataSource dataSource = createDataSource()) {
			DistributeApi distributeApi = distributeDto.getDistributeJson().getDistributeApi();
			//获取主数据信息
			DataGovernanceMasterModelDto modelDto = dataMasterApi.getMasterModelById(distributeDto.getMasterModelId()).getData();
			List<String> columns = modelDto.getColumns().stream().map(DataGovernanceMasterColumnDto::getName).collect(Collectors.toList());
			//读取数据库中的数据
			IDatabaseOperator sourceOperator = DatabaseOperatorFactory
					.createDatabaseOperator(dataSource, ProductTypeEnum.getByIndex(project.getDbType()));
			sourceOperator.setFetchSize(distributeApi.getFetchSize());
			try (StatementResultSet srs = sourceOperator.queryTableData(project.getDbSchema(), modelDto.getTableName(), columns); ResultSet rs = srs.getResultset()) {
				int size = 0;
				long dataCount = 0L;
				long bytes = 0;
				List<Map<String, Object>> rowList = new ArrayList<>();
				while (rs.next()) {
					size++;
					dataCount++;
					Map<String, Object> map = buildRowMap(columns, rs);
					bytes += SizeOf.newInstance().sizeOf(map);
					rowList.add(map);
					if (size % distributeApi.getFetchSize() == 0) {
						sendMsg(distributeApi, rowList);
					}
				}
				if (!rowList.isEmpty()) {
					sendMsg(distributeApi, rowList);
				}
				distributeLogDto.setEndTime(new Date());
				distributeLogDto.setRunStatus(CommonRunStatus.SUCCESS.getCode());
				distributeLogDto.setDataCount(dataCount);
				distributeLogDto.setByteCount(BytesUnitUtils.bytesSizeToHuman(bytes));
				dataMasterApi.upDistributeLog(distributeLogDto);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	@SneakyThrows
	private void sendMsg(DistributeApi distributeApi, List<Map<String, Object>> rowList) {
		List<ApiParam> headers = distributeApi.getHeaders();
		List<ApiParam> params = distributeApi.getParams();
		List<BasicHeader> basicHeaders = new ArrayList<>();
		if (!CollectionUtils.isEmpty(headers)) {
			for (ApiParam header : headers) {
				BasicHeader basicHeader = new BasicHeader(header.getKey(), header.getValue());
				basicHeaders.add(basicHeader);
			}
		}
		String url = distributeApi.getUrl();
		if (!CollectionUtils.isEmpty(params)) {
			StringBuilder builder = new StringBuilder();
			for (ApiParam param : params) {
				if (param.getValue() != null) {
					builder.append("&").append(param.getKey()).append("=").append(param.getValue());
				}
			}
			url = url + "?" + builder.toString().substring(1);
		}
		HttpResponse response = HttpUtil.post(url, SingletonObject.OBJECT_MAPPER.writeValueAsString(rowList), "application/json", basicHeaders.toArray(new Header[]{}));
		if (HttpStatus.SC_OK != response.getCode()) {
			throw new ServerException("request error:" + response.getResponseText());
		}
		rowList.clear();
	}
}
