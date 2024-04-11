package net.srt.service;

import net.srt.dto.SqlConsole;
import net.srt.dto.TableInfo;
import net.srt.framework.common.page.PageResult;
import net.srt.framework.common.utils.TreeNodeVo;
import net.srt.framework.mybatis.service.BaseService;
import net.srt.vo.ColumnDescriptionVo;
import net.srt.vo.DataDatabaseVO;
import net.srt.query.DataDatabaseQuery;
import net.srt.entity.DataDatabaseEntity;
import net.srt.vo.SchemaTableDataVo;
import net.srt.vo.SqlGenerationVo;
import net.srt.vo.TableVo;

import java.util.List;

/**
 * 数据集成-数据库管理
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-10-09
 */
public interface DataDatabaseService extends BaseService<DataDatabaseEntity> {

	PageResult<DataDatabaseVO> page(DataDatabaseQuery query);

	void save(DataDatabaseVO vo);

	void update(DataDatabaseVO vo);

	void delete(List<Long> idList);

	void testOnline(DataDatabaseVO vo);

	List<TableVo> getTablesById(Long id);

	SchemaTableDataVo getTableDataBySql(Integer id, SqlConsole sqlConsole);

	List<DataDatabaseVO> listAll();

	List<TreeNodeVo> listTree(Long id);

	List<ColumnDescriptionVo> getColumnInfo(Long id, String tableName);

	List<ColumnDescriptionVo> getColumnInfoBySql(Long id, SqlConsole sqlConsole);

	SqlGenerationVo getSqlGeneration(Long id, String tableName, String tableRemarks);

	List<TreeNodeVo> listMiddleDbTree();

	List<ColumnDescriptionVo> middleDbClumnInfo(String tableName);

	SqlGenerationVo getMiddleDbSqlGeneration(String tableName, String tableRemarks);

	TableInfo getTableInfo(String tableName);

	TableInfo saveTableInfo(TableInfo tableInfo);

	void deleteTableInfo(String tableName);


}
