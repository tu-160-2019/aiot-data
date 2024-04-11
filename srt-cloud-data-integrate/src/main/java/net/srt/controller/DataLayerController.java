package net.srt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import net.srt.convert.DataLayerConvert;
import net.srt.entity.DataLayerEntity;
import net.srt.framework.common.page.PageResult;
import net.srt.framework.common.utils.Result;
import net.srt.query.DataLayerQuery;
import net.srt.service.DataLayerService;
import net.srt.vo.DataLayerVO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
* 数仓分层
*
* @author zrx 985134801@qq.com
* @since 1.0.0 2022-10-08
*/
@RestController
@RequestMapping("layer")
@Tag(name="数仓分层")
@AllArgsConstructor
public class DataLayerController {
    private final DataLayerService dataLayerService;

    @GetMapping("page")
    @Operation(summary = "分页")
    @PreAuthorize("hasAuthority('data-integrate:layer:page')")
    public Result<PageResult<DataLayerVO>> page(@Valid DataLayerQuery query){
        PageResult<DataLayerVO> page = dataLayerService.page(query);

        return Result.ok(page);
    }

    @GetMapping("{id}")
    @Operation(summary = "信息")
    @PreAuthorize("hasAuthority('data-integrate:layer:info')")
    public Result<DataLayerVO> get(@PathVariable("id") Long id){
        DataLayerEntity entity = dataLayerService.getById(id);

        return Result.ok(DataLayerConvert.INSTANCE.convert(entity));
    }

    /*@PostMapping
    @Operation(summary = "保存")
    @PreAuthorize("hasAuthority('data-integrate:layer:save')")
    public Result<String> save(@RequestBody DataLayerVO vo){
        dataLayerService.save(vo);

        return Result.ok();
    }*/

    @PutMapping
    @Operation(summary = "修改")
    @PreAuthorize("hasAuthority('data-integrate:layer:update')")
    public Result<String> update(@RequestBody @Valid DataLayerVO vo){
        dataLayerService.update(vo);

        return Result.ok();
    }

    /*@DeleteMapping
    @Operation(summary = "删除")
    @PreAuthorize("hasAuthority('data-integrate:layer:delete')")
    public Result<String> delete(@RequestBody List<Long> idList){
        dataLayerService.delete(idList);

        return Result.ok();
    }*/
}
