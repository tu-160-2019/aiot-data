package net.srt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import net.srt.entity.DataFileCategoryEntity;
import net.srt.framework.common.utils.BeanUtil;
import net.srt.framework.common.utils.Result;
import net.srt.framework.common.utils.TreeNodeVo;
import net.srt.service.DataFileCategoryService;
import net.srt.vo.DataFileCategoryVO;
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
 * 文件分组表
 *
 * @author zrx 985134801@qq.com
 * @since 1.0.0 2022-11-12
 */
@RestController
@RequestMapping("fileCategory")
@Tag(name = "文件分组表")
@AllArgsConstructor
public class DataFileCategoryController {
	private final DataFileCategoryService dataFileCategoryService;

	@GetMapping
	@Operation(summary = "查询文件分组树")
	public Result<List<TreeNodeVo>> listTree() {
		return Result.ok(dataFileCategoryService.listTree());
	}

	@GetMapping("/{id}")
	@Operation(summary = "根据id获取")
	public Result<TreeNodeVo> getById(@PathVariable Integer id) {
		DataFileCategoryEntity entity = dataFileCategoryService.getById(id);
		TreeNodeVo nodeVo = BeanUtil.copyProperties(entity, TreeNodeVo::new);
		nodeVo.setLabel(entity.getName());
		nodeVo.setParentPath(entity.getPath().contains("/") ? entity.getPath().substring(0, entity.getPath().lastIndexOf("/")) : null);
		return Result.ok(nodeVo);
	}


	@PostMapping
	@Operation(summary = "保存")
	@PreAuthorize("hasAuthority('data-integrate:fileCategory:save')")
	public Result<String> save(@RequestBody DataFileCategoryVO vo) {
		dataFileCategoryService.save(vo);
		return Result.ok();
	}

	@PutMapping
	@Operation(summary = "修改")
	@PreAuthorize("hasAuthority('data-integrate:fileCategory:update')")
	public Result<String> update(@RequestBody @Valid DataFileCategoryVO vo) {
		dataFileCategoryService.update(vo);
		return Result.ok();
	}

	@DeleteMapping
	@Operation(summary = "删除")
	@PreAuthorize("hasAuthority('data-integrate:fileCategory:delete')")
	public Result<String> delete(Long id) {
		dataFileCategoryService.delete(id);
		return Result.ok();
	}
}
