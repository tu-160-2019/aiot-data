package net.srt.service;

import net.srt.entity.DataTableEntity;
import net.srt.framework.common.page.PageResult;
import net.srt.framework.mybatis.service.BaseService;
import net.srt.query.DataTableQuery;
import net.srt.vo.ColumnDescriptionVo;
import net.srt.vo.DataTableVO;
import net.srt.vo.SchemaTableDataVo;

import java.util.List;

/**
 * 数据集成-贴源数据
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-11-07
 */
public interface DataTableService extends BaseService<DataTableEntity> {

	PageResult<DataTableVO> page(DataTableQuery query);

	void save(DataTableVO vo);

	void update(DataTableVO vo);

	void delete(List<Long> idList);

	DataTableEntity getByTableName(Long projectId, String tableName);

	List<ColumnDescriptionVo> getColumnInfo(String tableName);

	SchemaTableDataVo getTableData(String tableName);
}
