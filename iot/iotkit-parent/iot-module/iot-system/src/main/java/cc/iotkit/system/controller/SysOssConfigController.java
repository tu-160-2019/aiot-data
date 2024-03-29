package cc.iotkit.system.controller;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.api.Request;
import cc.iotkit.common.log.annotation.Log;
import cc.iotkit.common.log.enums.BusinessType;
import cc.iotkit.common.validate.AddGroup;
import cc.iotkit.common.validate.EditGroup;
import cc.iotkit.common.web.core.BaseController;
import cc.iotkit.system.dto.bo.SysOssConfigBo;
import cc.iotkit.system.dto.vo.SysOssConfigVo;
import cc.iotkit.system.service.ISysOssConfigService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 对象存储配置
 *
 * @author Lion Li
 * @author 孤舟烟雨
 * @date 2021-08-13
 */
@Validated
@RequiredArgsConstructor
@RestController
@Controller
@ResponseBody
@RequestMapping("/resource/oss/config")
public class SysOssConfigController extends BaseController {

    private final ISysOssConfigService ossConfigService;

    /**
     * 查询对象存储配置列表
     */
    @ApiOperation(value = "查询对象存储配置列表", notes = "查询对象存储配置列表")
    @SaCheckPermission("system:oss:list")
    @PostMapping("/list")
    public Paging<SysOssConfigVo> list(@Validated @RequestBody PageRequest<SysOssConfigBo> query) {
        return ossConfigService.queryPageList(query);
    }

    /**
     * 获取对象存储配置详细信息
     *
     */
    @ApiOperation(value = "获取对象存储配置详细信息", notes = "获取对象存储配置详细信息")
    @SaCheckPermission("system:oss:query")
    @PostMapping("/getDetail")
    public SysOssConfigVo getInfo(@Validated @RequestBody Request<Long> bo) {
        return ossConfigService.queryById(bo.getData());
    }

    /**
     * 新增对象存储配置
     */
    @ApiOperation(value = "新增对象存储配置", notes = "新增对象存储配置")
    @SaCheckPermission("system:oss:add")
    @Log(title = "对象存储配置", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public void add(@Validated(AddGroup.class) @RequestBody Request<SysOssConfigBo> bo) {
        ossConfigService.insertByBo(bo.getData());
    }

    /**
     * 修改对象存储配置
     */
    @ApiOperation(value = "修改对象存储配置", notes = "修改对象存储配置")
    @SaCheckPermission("system:oss:edit")
    @Log(title = "对象存储配置", businessType = BusinessType.UPDATE)
    @PostMapping()
    public void edit(@Validated(EditGroup.class) @RequestBody Request<SysOssConfigBo> bo) {
        ossConfigService.updateByBo(bo.getData());
    }

    /**
     * 删除对象存储配置
     *
     */
    @ApiOperation(value = "删除对象存储配置", notes = "删除对象存储配置")
    @SaCheckPermission("system:oss:remove")
    @Log(title = "对象存储配置", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public void remove(@Validated @RequestBody Request<List<Long>> bo) {
        ossConfigService.deleteWithValidByIds(bo.getData(), true);
    }

    /**
     * 状态修改
     */
    @ApiOperation(value = "状态修改", notes = "状态修改")
    @SaCheckPermission("system:oss:edit")
    @Log(title = "对象存储状态修改", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public void changeStatus(@RequestBody Request<SysOssConfigBo> bo) {
        ossConfigService.updateOssConfigStatus(bo.getData());
    }
}
