package net.srt.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import net.srt.framework.common.constant.Constant;
import net.srt.framework.common.utils.Result;
import net.srt.system.convert.SysOrgConvert;
import net.srt.system.entity.SysOrgEntity;
import net.srt.system.service.SysOrgService;
import net.srt.system.vo.SysOrgVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 机构管理
 *
 * @author 阿沐 babamu@126.com
 */
@RestController
@RequestMapping("org")
@Tag(name = "机构管理")
@AllArgsConstructor
public class SysOrgController {
	private final SysOrgService sysOrgService;

	@GetMapping("list")
	@Operation(summary = "列表")
	public Result<List<SysOrgVO>> list(Long parentId) {
		List<SysOrgVO> list = sysOrgService.getList(parentId);

		return Result.ok(list);
	}

	@GetMapping("{id}")
	@Operation(summary = "信息")
	@PreAuthorize("hasAuthority('sys:org:info')")
	public Result<SysOrgVO> get(@PathVariable("id") Long id) {
		SysOrgEntity entity = sysOrgService.getById(id);
		SysOrgVO vo = SysOrgConvert.INSTANCE.convert(entity);

		// 获取上级机构名称
		if (!Constant.ROOT.equals(entity.getPid())) {
			SysOrgEntity parentEntity = sysOrgService.getById(entity.getPid());
			vo.setParentName(parentEntity.getName());
		}

		return Result.ok(vo);
	}

	@PostMapping
	@Operation(summary = "保存")
	@PreAuthorize("hasAuthority('sys:org:save')")
	public Result<String> save(@RequestBody @Valid SysOrgVO vo) {
		sysOrgService.save(vo);

		return Result.ok();
	}

	@PutMapping
	@Operation(summary = "修改")
	@PreAuthorize("hasAuthority('sys:org:update')")
	public Result<String> update(@RequestBody @Valid SysOrgVO vo) {
		sysOrgService.update(vo);

		return Result.ok();
	}

	@DeleteMapping("{id}")
	@Operation(summary = "删除")
	@PreAuthorize("hasAuthority('sys:org:delete')")
	public Result<String> delete(@PathVariable("id") Long id) {
		sysOrgService.delete(id);

		return Result.ok();
	}

	@GetMapping("list-all")
	@Operation(summary = "获取全部列表")
	public Result<List<SysOrgVO>> listAll() {
		return Result.ok(SysOrgConvert.INSTANCE.convertList(sysOrgService.list()));
	}

}
