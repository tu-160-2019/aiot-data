package net.srt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import net.srt.dto.DataAccessClientDto;
import net.srt.dto.PreviewMapDto;
import net.srt.framework.common.page.PageResult;
import net.srt.framework.common.utils.Result;
import net.srt.query.DataAccessQuery;
import net.srt.query.DataAccessTaskDetailQuery;
import net.srt.query.DataAccessTaskQuery;
import net.srt.service.DataAccessService;
import net.srt.vo.DataAccessTaskDetailVO;
import net.srt.vo.DataAccessTaskVO;
import net.srt.vo.DataAccessVO;
import net.srt.vo.PreviewNameMapperVo;
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
 * 数据集成-数据接入
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-10-24
 */
@RestController
@RequestMapping("access")
@Tag(name = "数据集成-数据接入")
@AllArgsConstructor
public class DataAccessController {
	private final DataAccessService dataAccessService;

	@GetMapping("page")
	@Operation(summary = "分页")
	@PreAuthorize("hasAuthority('data-integrate:access:page')")
	public Result<PageResult<DataAccessVO>> page(@Valid DataAccessQuery query) {
		PageResult<DataAccessVO> page = dataAccessService.page(query);

		return Result.ok(page);
	}

	@GetMapping("{id}")
	@Operation(summary = "信息")
	@PreAuthorize("hasAuthority('data-integrate:access:info')")
	public Result<DataAccessClientDto> get(@PathVariable("id") Long id) {
		return Result.ok(dataAccessService.getById(id));
	}

	@PostMapping
	@Operation(summary = "保存")
	@PreAuthorize("hasAuthority('data-integrate:access:save')")
	public Result<String> save(@RequestBody DataAccessClientDto dto) {
		dataAccessService.save(dto);
		return Result.ok();
	}

	@PutMapping
	@Operation(summary = "修改")
	@PreAuthorize("hasAuthority('data-integrate:access:update')")
	public Result<String> update(@RequestBody DataAccessClientDto dto) {
		dataAccessService.update(dto);

		return Result.ok();
	}

	@DeleteMapping
	@Operation(summary = "删除")
	@PreAuthorize("hasAuthority('data-integrate:access:delete')")
	public Result<String> delete(@RequestBody List<Long> idList) {
		dataAccessService.delete(idList);
		return Result.ok();
	}

	@PostMapping("preview-table-name-map")
	@Operation(summary = "预览表名映射")
	public Result<List<PreviewNameMapperVo>> previewTableMap(@RequestBody PreviewMapDto previewMapDto) {
		return Result.ok(dataAccessService.previewTableMap(previewMapDto));
	}

	@PostMapping("preview-column-name-map")
	@Operation(summary = "预览字段名映射")
	public Result<List<PreviewNameMapperVo>> previewColumnMap(@RequestBody PreviewMapDto previewMapDto) {
		return Result.ok(dataAccessService.previewColumnMap(previewMapDto));
	}

	@PostMapping("release/{id}")
	@Operation(summary = "发布任务")
	@PreAuthorize("hasAuthority('data-integrate:access:release')")
	public Result<String> release(@PathVariable Long id) {
		dataAccessService.release(id);
		return Result.ok();
	}

	@PostMapping("cancle/{id}")
	@Operation(summary = "取消任务")
	@PreAuthorize("hasAuthority('data-integrate:access:cancle')")
	public Result<String> cancle(@PathVariable Long id) {
		dataAccessService.cancle(id);
		return Result.ok();
	}

	@PostMapping("hand-run/{id}")
	@Operation(summary = "手动调度执行")
	@PreAuthorize("hasAuthority('data-integrate:access:selfhandler')")
	public Result<String> handRun(@PathVariable Long id) {
		dataAccessService.handRun(id);
		return Result.ok();
	}

	@PostMapping("stop-task/{executeNo}")
	@Operation(summary = "根据手动执行的编号停止任务")
	public Result<String> stopHandTask(@PathVariable String executeNo) {
		dataAccessService.stopHandTask(executeNo);
		return Result.ok();
	}

	@GetMapping("task-page")
	@Operation(summary = "获取调度记录")
	public Result<PageResult<DataAccessTaskVO>> taskPage(DataAccessTaskQuery taskQuery) {
		PageResult<DataAccessTaskVO> pageResult = dataAccessService.taskPage(taskQuery);
		return Result.ok(pageResult);
	}

	@GetMapping("task/{id}")
	@Operation(summary = "获取调度任务")
	public Result<DataAccessTaskVO> getTaskById(@PathVariable Long id) {
		return Result.ok(dataAccessService.getTaskById(id));
	}

	@DeleteMapping("task")
	@Operation(summary = "删除调度记录")
	public Result<String> deleteTask(@RequestBody List<Long> idList) {
		dataAccessService.deleteTask(idList);
		return Result.ok();
	}

	@GetMapping("task-detail-page")
	@Operation(summary = "获取同步结果")
	public Result<PageResult<DataAccessTaskDetailVO>> taskDetailPage(DataAccessTaskDetailQuery detailQuery) {
		PageResult<DataAccessTaskDetailVO> pageResult = dataAccessService.taskDetailPage(detailQuery);
		return Result.ok(pageResult);
	}

}
