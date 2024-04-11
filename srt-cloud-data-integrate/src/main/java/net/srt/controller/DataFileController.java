package net.srt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import net.srt.convert.DataFileConvert;
import net.srt.entity.DataFileEntity;
import net.srt.framework.common.page.PageResult;
import net.srt.framework.common.utils.Result;
import net.srt.service.DataFileService;
import net.srt.query.DataFileQuery;
import net.srt.vo.DataFileVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
* 文件表
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-11-16
*/
@RestController
@RequestMapping("file")
@Tag(name="文件表")
@AllArgsConstructor
public class DataFileController {
    private final DataFileService dataFileService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @PreAuthorize("hasAuthority('data-integrate:file:page')")
    public Result<PageResult<DataFileVO>> page(@Valid DataFileQuery query){
        PageResult<DataFileVO> page = dataFileService.page(query);

        return Result.ok(page);
    }

	@GetMapping("page-resource")
	@Operation(summary = "根据resourceId分页获取")
	public Result<PageResult<DataFileVO>> pageResource(@Valid DataFileQuery query){
		PageResult<DataFileVO> page = dataFileService.pageResource(query);

		return Result.ok(page);
	}

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('data-integrate:file:info')")
    public Result<DataFileVO> get(@PathVariable("id") Long id){
        DataFileEntity entity = dataFileService.getById(id);

        return Result.ok(DataFileConvert.INSTANCE.convert(entity));
    }

    @PostMapping
    @Operation(summary = "保存")
    @PreAuthorize("hasAuthority('data-integrate:file:save')")
    public Result<String> save(@RequestBody DataFileVO vo){
        dataFileService.save(vo);

        return Result.ok();
    }

    @PutMapping
    @Operation(summary = "修改")
    @PreAuthorize("hasAuthority('data-integrate:file:update')")
    public Result<String> update(@RequestBody @Valid DataFileVO vo){
        dataFileService.update(vo);

        return Result.ok();
    }

    @DeleteMapping
    @Operation(summary = "删除")
    @PreAuthorize("hasAuthority('data-integrate:file:delete')")
    public Result<String> delete(@RequestBody List<Long> idList){
        dataFileService.delete(idList);

        return Result.ok();
    }
}
