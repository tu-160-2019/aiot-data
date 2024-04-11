package net.srt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import net.srt.convert.DataDatabaseConvert;
import net.srt.dto.SqlConsole;
import net.srt.dto.TableInfo;
import net.srt.entity.DataDatabaseEntity;
import net.srt.framework.common.page.PageResult;
import net.srt.framework.common.utils.Result;
import net.srt.framework.common.utils.TreeNodeVo;
import net.srt.query.DataDatabaseQuery;
import net.srt.service.DataDatabaseService;
import net.srt.vo.ColumnDescriptionVo;
import net.srt.vo.DataDatabaseVO;
import net.srt.vo.SchemaTableDataVo;
import net.srt.vo.SqlGenerationVo;
import net.srt.vo.TableVo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 数据集成-数据库管理
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-10-09
 */
@RestController
@RequestMapping("database")
@Tag(name = "数据集成-数据库管理")
@AllArgsConstructor
public class DataDatabaseController {
	private final DataDatabaseService dataDatabaseService;

	@GetMapping("page")
	@Operation(summary = "分页")
	@PreAuthorize("hasAuthority('data-integrate:database:page')")
	public Result<PageResult<DataDatabaseVO>> page(@Valid DataDatabaseQuery query) {
		PageResult<DataDatabaseVO> page = dataDatabaseService.page(query);

		return Result.ok(page);
	}

	@GetMapping("{id}")
	@Operation(summary = "信息")
	@PreAuthorize("hasAuthority('data-integrate:database:info')")
	public Result<DataDatabaseVO> get(@PathVariable("id") Long id) {
		DataDatabaseEntity entity = dataDatabaseService.getById(id);

		return Result.ok(DataDatabaseConvert.INSTANCE.convert(entity));
	}

	@PostMapping
	@Operation(summary = "保存")
	@PreAuthorize("hasAuthority('data-integrate:database:save')")
	public Result<String> save(@RequestBody DataDatabaseVO vo) {
		dataDatabaseService.save(vo);

		return Result.ok();
	}

	@PutMapping
	@Operation(summary = "修改")
	@PreAuthorize("hasAuthority('data-integrate:database:update')")
	public Result<String> update(@RequestBody @Valid DataDatabaseVO vo) {
		dataDatabaseService.update(vo);

		return Result.ok();
	}

	@DeleteMapping
	@Operation(summary = "删除")
	@PreAuthorize("hasAuthority('data-integrate:database:delete')")
	public Result<String> delete(@RequestBody List<Long> idList) {
		dataDatabaseService.delete(idList);
		return Result.ok();
	}

	@PostMapping("/test-online")
	@Operation(summary = "测试连接")
	public Result<String> testOnline(@RequestBody @Valid DataDatabaseVO vo) {
		dataDatabaseService.testOnline(vo);
		return Result.ok();
	}

	@GetMapping("/tables/{id}")
	@Operation(summary = "根据数据库id获取表相关信息")
	public Result<List<TableVo>> getTablesById(@PathVariable Long id) {
		List<TableVo> tableVos = dataDatabaseService.getTablesById(id);
		return Result.ok(tableVos);
	}

	@PostMapping("/table-data/{id}")
	@Operation(summary = "根据sql获取数据")
	public Result<SchemaTableDataVo> getTableDataBySql(@PathVariable Integer id, @RequestBody SqlConsole sqlConsole) {
		SchemaTableDataVo schemaTableDataVo = dataDatabaseService.getTableDataBySql(id, sqlConsole);
		return Result.ok(schemaTableDataVo);
	}

	@GetMapping("/list-all")
	@Operation(summary = "获取当前用户所能看到的的数据表")
	public Result<List<DataDatabaseVO>> listAll() {
		List<DataDatabaseVO> list = dataDatabaseService.listAll();
		return Result.ok(list);
	}

	@GetMapping("/list-tree/{id}")
	@Operation(summary = "获取库目录树")
	public Result<List<TreeNodeVo>> listTree(@PathVariable Long id) {
		List<TreeNodeVo> list = dataDatabaseService.listTree(id);
		return Result.ok(list);
	}

	@GetMapping("/middle-db/list-tree")
	@Operation(summary = "获取中台库（当前项目）目录树")
	public Result<List<TreeNodeVo>> listMiddleDbTree() {
		List<TreeNodeVo> list = dataDatabaseService.listMiddleDbTree();
		return Result.ok(list);
	}

	@PostMapping("/{id}/sql/columns")
	@Operation(summary = "获取sql获取字段信息")
	public Result<List<ColumnDescriptionVo>> columnInfoBySql(@PathVariable Long id, @RequestBody SqlConsole sqlConsole) {
		return Result.ok(dataDatabaseService.getColumnInfoBySql(id, sqlConsole));
	}

	@GetMapping("/{id}/{tableName}/columns")
	@Operation(summary = "获取字段信息")
	public Result<List<ColumnDescriptionVo>> columnInfo(@PathVariable Long id, @PathVariable String tableName) {
		return Result.ok(dataDatabaseService.getColumnInfo(id, tableName));
	}

	@GetMapping("/middle-db/{tableName}/columns")
	@Operation(summary = "获取中台库字段信息")
	public Result<List<ColumnDescriptionVo>> middleDbClumnInfo(@PathVariable String tableName) {
		return Result.ok(dataDatabaseService.middleDbClumnInfo(tableName));
	}

	@GetMapping("/{id}/{tableName}/sql-generation")
	@Operation(summary = "获取sql信息")
	public Result<SqlGenerationVo> getSqlGeneration(@PathVariable Long id, @PathVariable String tableName, String tableRemarks) {
		return Result.ok(dataDatabaseService.getSqlGeneration(id, tableName, tableRemarks));
	}

	@GetMapping("/middle-db/{tableName}/sql-generation")
	@Operation(summary = "获取中台库sql信息")
	public Result<SqlGenerationVo> getMiddleDbSqlGeneration(@PathVariable String tableName, String tableRemarks) {
		return Result.ok(dataDatabaseService.getMiddleDbSqlGeneration(tableName, tableRemarks));
	}

	@GetMapping("/middle-db/table-info/{tableName}")
	@Operation(summary = "获取表信息")
	public Result<TableInfo> getTableInfo(@PathVariable String tableName) {
		return Result.ok(dataDatabaseService.getTableInfo(tableName));
	}

	@PostMapping("/middle-db/table-info")
	@Operation(summary = "保存表（建表）")
	public Result<TableInfo> saveTableInfo(@RequestBody TableInfo tableInfo) {
		return Result.ok(dataDatabaseService.saveTableInfo(tableInfo));
	}

	@DeleteMapping("/middle-db/table-info/{tableName}")
	@Operation(summary = "删除表")
	public Result<String> deleteTableInfo(@PathVariable String tableName) {
		dataDatabaseService.deleteTableInfo(tableName);
		return Result.ok();
	}

}
