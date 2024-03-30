package cc.iotkit.system.controller;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.api.Request;
import cc.iotkit.common.excel.utils.ExcelUtil;
import cc.iotkit.common.log.annotation.Log;
import cc.iotkit.common.log.enums.BusinessType;
import cc.iotkit.system.dto.bo.SysDictDataBo;
import cc.iotkit.system.dto.vo.SysDictDataVo;
import cc.iotkit.system.service.ISysDictDataService;
import cc.iotkit.system.service.ISysDictTypeService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据字典信息
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController {

    private final ISysDictDataService dictDataService;
    private final ISysDictTypeService dictTypeService;

    /**
     * 查询字典数据列表
     */
    @SaCheckPermission("system:dict:list")
    @ApiOperation(value = "查询字典数据列表", notes = "查询字典数据列表")
    @PostMapping("/list")
    public Paging<SysDictDataVo> list(@Validated @RequestBody  PageRequest<SysDictDataBo> query) {
        return dictDataService.selectPageDictDataList( query);
    }

    /**
     * 导出字典数据列表
     */
    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:dict:export")
    @ApiOperation(value = "导出字典数据列表", notes = "导出字典数据列表")
    @PostMapping("/export")
    public void export(SysDictDataBo bo, HttpServletResponse response) {
        List<SysDictDataVo> list = dictDataService.selectDictDataList(bo);
        ExcelUtil.exportExcel(list, "字典数据", SysDictDataVo.class, response);
    }

    /**
     * 查询字典数据详细
     *

     */
    @SaCheckPermission("system:dict:query")
    @ApiOperation(value = "查询字典数据详细", notes = "查询字典数据详细")
    @PostMapping(value = "/getDetail")
    public SysDictDataVo getInfo(@Validated @RequestBody Request<Long> bo) {
        return dictDataService.selectDictDataById(bo.getData());
    }

    /**
     * 根据字典类型查询字典数据信息
     *

     */
    @ApiOperation(value = "根据字典类型查询字典数据信息", notes = "根据字典类型查询字典数据信息")
    @PostMapping(value = "/type")
    public List<SysDictDataVo> dictType(@Validated @RequestBody Request<String> bo) {
        String dictType = bo.getData();
        List<SysDictDataVo> data = dictTypeService.selectDictDataByType(dictType);
        if (ObjectUtil.isNull(data)) {
            data = new ArrayList<>();
        }
        return data;
    }

    /**
     * 新增字典类型
     */
    @ApiOperation(value = "新增字典类型", notes = "新增字典类型")
    @SaCheckPermission("system:dict:add")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public void add(@Validated @RequestBody Request<SysDictDataBo> bo) {
        dictDataService.insertDictData(bo.getData());
    }

    /**
     * 修改保存字典类型
     */
    @ApiOperation(value = "修改保存字典类型", notes = "修改保存字典类型")
    @SaCheckPermission("system:dict:edit")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PutMapping("/edit")
    public void edit(@Validated @RequestBody Request<SysDictDataBo> bo) {
        dictDataService.updateDictData(bo.getData());
    }

    /**
     * 删除字典类型
     *
     */
    @ApiOperation(value = "删除字典类型", notes = "删除字典类型")
    @SaCheckPermission("system:dict:remove")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public void remove(@Validated @RequestBody Request<Long[]> bo) {
        dictDataService.deleteDictDataByIds(bo.getData());
    }
}
