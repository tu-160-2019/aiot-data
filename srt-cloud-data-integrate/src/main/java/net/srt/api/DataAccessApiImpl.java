package net.srt.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import net.srt.api.module.data.integrate.DataAccessApi;
import net.srt.api.module.data.integrate.dto.DataAccessDto;
import net.srt.api.module.data.integrate.dto.DataAccessIncreaseLogDto;
import net.srt.api.module.data.integrate.dto.DataAccessTaskDto;
import net.srt.api.module.data.integrate.dto.PreviewNameMapperDto;
import net.srt.api.module.data.integrate.dto.QueryIncreaseLog;
import net.srt.constants.YesOrNo;
import net.srt.convert.DataAccessConvert;
import net.srt.convert.DataAccessIncreaseLogConvert;
import net.srt.convert.DataAccessTaskConvert;
import net.srt.dao.DataAccessIncreaseLogDao;
import net.srt.entity.DataAccessEntity;
import net.srt.entity.DataAccessIncreaseLogEntity;
import net.srt.entity.DataAccessTaskDetailEntity;
import net.srt.entity.DataAccessTaskEntity;
import net.srt.framework.common.utils.Result;
import net.srt.service.DataAccessService;
import net.srt.service.DataAccessTaskDetailService;
import net.srt.service.DataAccessTaskService;
import org.springframework.web.bind.annotation.RestController;
import srt.cloud.framework.dbswitch.data.domain.DbSwitchTableResult;
import srt.cloud.framework.dbswitch.data.util.BytesUnitUtils;

import java.util.Date;
import java.util.List;

/**
 * @ClassName DataAccessApiImpl
 * @Author zrx
 * @Date 2022/10/26 11:50
 */
@RestController
@RequiredArgsConstructor
public class DataAccessApiImpl implements DataAccessApi {

	private final DataAccessService dataAccessService;
	private final DataAccessTaskService dataAccessTaskService;
	private final DataAccessTaskDetailService dataAccessTaskDetailService;
	private final DataAccessIncreaseLogDao increaseLogDao;

	@Override
	public Result<DataAccessDto> getById(Long id) {
		DataAccessEntity dataAccessEntity = dataAccessService.loadById(id);
		return Result.ok(DataAccessConvert.INSTANCE.convertDto(dataAccessEntity));
	}

	@Override
	public Result<Long> addTask(DataAccessTaskDto dataAccessTaskDto) {
		DataAccessTaskEntity dataAccessTaskEntity = DataAccessTaskConvert.INSTANCE.convertByDto(dataAccessTaskDto);
		dataAccessTaskService.save(dataAccessTaskEntity);
		//更新任务的最新开始时间和状态
		dataAccessService.updateStartInfo(dataAccessTaskDto.getDataAccessId());
		return Result.ok(dataAccessTaskEntity.getId());
	}

	@Override
	public void updateTask(DataAccessTaskDto dataAccessTaskDto) {
		DataAccessTaskEntity dataAccessTaskEntity = DataAccessTaskConvert.INSTANCE.convertByDto(dataAccessTaskDto);
		dataAccessTaskService.updateById(dataAccessTaskEntity);
		//更新任务的最新结束时间和状态
		if (dataAccessTaskDto.getUpdateTaskAccess()) {
			dataAccessService.updateEndInfo(dataAccessTaskDto.getDataAccessId(), dataAccessTaskDto.getRunStatus(), dataAccessTaskDto.getNextRunTime());
		}
	}

	@Override
	public void addTaskDetail(Long projectId, Long orgId, Long creator, Long taskId, Long dataAccessId, DbSwitchTableResult tableResult) {
		dataAccessTaskDetailService.save(DataAccessTaskDetailEntity.builder().projectId(projectId).orgId(orgId).creator(creator).taskId(taskId).dataAccessId(dataAccessId)
				.sourceSchemaName(tableResult.getSourceSchemaName()).sourceTableName(tableResult.getSourceTableName())
				.targetSchemaName(tableResult.getTargetSchemaName()).targetTableName(tableResult.getTargetTableName())
				.ifSuccess(tableResult.getIfSuccess().get() ? YesOrNo.YES.getValue() : YesOrNo.NO.getValue())
				.syncCount(tableResult.getSyncCount().get()).syncBytes(BytesUnitUtils.bytesSizeToHuman(tableResult.getSyncBytes().get()))
				.createTime(new Date()).errorMsg(tableResult.getErrorMsg()).successMsg(tableResult.getSuccessMsg()).build());
	}

	@Override
	public Result<DataAccessTaskDto> getTaskById(Long id) {
		return Result.ok(DataAccessTaskConvert.INSTANCE.convertDto(dataAccessTaskService.getById(id)));
	}

	@Override
	public Result<List<PreviewNameMapperDto>> getTableMap(Long id) {
		List<PreviewNameMapperDto> previewNameMapperDtos = dataAccessService.getTableMap(id);
		return Result.ok(previewNameMapperDtos);
	}

	@Override
	public Result<List<PreviewNameMapperDto>> getColumnMap(Long id, String tableName) {
		List<PreviewNameMapperDto> previewNameMapperDtos = dataAccessService.getColumnMap(id, tableName);
		return Result.ok(previewNameMapperDtos);
	}

	@Override
	public DataAccessIncreaseLogDto getNewestLog(QueryIncreaseLog queryIncreaseLog) {
		LambdaQueryWrapper<DataAccessIncreaseLogEntity> queryWrapper = Wrappers.lambdaQuery();
		queryWrapper.eq(DataAccessIncreaseLogEntity::getDataAccessId, queryIncreaseLog.getDataAccessId())
				.eq(DataAccessIncreaseLogEntity::getSourceDatabaseId, queryIncreaseLog.getSourceDatabaseId());
		if (queryIncreaseLog.getTargetDatabaseId() != null) {
			queryWrapper.eq(DataAccessIncreaseLogEntity::getTargetDatabaseId, queryIncreaseLog.getTargetDatabaseId());
		} else {
			queryWrapper.isNull(DataAccessIncreaseLogEntity::getTargetDatabaseId);
		}
		queryWrapper.eq(DataAccessIncreaseLogEntity::getSchemaName, queryIncreaseLog.getSchemaName()).eq(DataAccessIncreaseLogEntity::getTableName, queryIncreaseLog.getTableName())
				.eq(DataAccessIncreaseLogEntity::getIncreaseColumn, queryIncreaseLog.getIncreaseColumn()).orderByDesc(DataAccessIncreaseLogEntity::getId).last("limit 1");
		DataAccessIncreaseLogEntity dataAccessIncreaseLogEntity = increaseLogDao.selectOne(queryWrapper);
		return DataAccessIncreaseLogConvert.INSTANCE.convert(dataAccessIncreaseLogEntity);
	}

	@Override
	public void addIncreaseLog(DataAccessIncreaseLogDto increaseLogDto) {
		increaseLogDao.insert(DataAccessIncreaseLogConvert.INSTANCE.convert(increaseLogDto));
	}
}
