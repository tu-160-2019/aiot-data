package net.srt.service;

import net.srt.api.module.data.integrate.dto.PreviewNameMapperDto;
import net.srt.dto.DataAccessClientDto;
import net.srt.dto.PreviewMapDto;
import net.srt.framework.common.page.PageResult;
import net.srt.framework.mybatis.service.BaseService;
import net.srt.query.DataAccessTaskDetailQuery;
import net.srt.query.DataAccessTaskQuery;
import net.srt.vo.DataAccessTaskDetailVO;
import net.srt.vo.DataAccessTaskVO;
import net.srt.vo.DataAccessVO;
import net.srt.query.DataAccessQuery;
import net.srt.entity.DataAccessEntity;
import net.srt.vo.PreviewNameMapperVo;

import java.util.Date;
import java.util.List;

/**
 * 数据集成-数据接入
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-10-24
 */
public interface DataAccessService extends BaseService<DataAccessEntity> {

	PageResult<DataAccessVO> page(DataAccessQuery query);

	DataAccessClientDto getById(Long id);

	void save(DataAccessClientDto dto);

	void update(DataAccessClientDto dto);

	void delete(List<Long> idList);

	DataAccessEntity loadById(Long id);

	void updateStartInfo(Long dataAccessId);

	void updateEndInfo(Long dataAccessId, Integer runStatus, Date nextRunTime);

	List<PreviewNameMapperDto> getTableMap(Long id);

	List<PreviewNameMapperVo> previewTableMap(PreviewMapDto previewMapDto);

	List<PreviewNameMapperDto> getColumnMap(Long id, String tableName);

	List<PreviewNameMapperVo> previewColumnMap(PreviewMapDto previewMapDto);

	void release(Long id);

	void cancle(Long id);

	void handRun(Long id);

	void stopHandTask(String executeNo);

	PageResult<DataAccessTaskVO> taskPage(DataAccessTaskQuery taskQuery);

	void deleteTask(List<Long> idList);

	PageResult<DataAccessTaskDetailVO> taskDetailPage(DataAccessTaskDetailQuery detailQuery);

	DataAccessTaskVO getTaskById(Long id);


}
