package cc.iotkit.manager.controller;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.api.Request;
import cc.iotkit.common.log.annotation.Log;
import cc.iotkit.common.log.enums.BusinessType;
import cc.iotkit.common.validate.AddGroup;
import cc.iotkit.common.validate.EditGroup;
import cc.iotkit.common.validate.QueryGroup;
import cc.iotkit.manager.dto.bo.plugin.PluginInfoBo;
import cc.iotkit.manager.dto.vo.plugin.PluginInfoVo;
import cc.iotkit.manager.service.IPluginService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author sjg
 */
@Api(tags = {"插件管理"})
@Slf4j
@RestController
@RequestMapping("/plugin")
public class PluginController {

    @Autowired
    private IPluginService pluginService;

    @ApiOperation("上传Jar包")
    @SaCheckPermission("iot:plugin:add")
    @PostMapping("/uploadJar")
    public void uploadJar(
            @RequestParam("id") Long id,
            @RequestPart("file") MultipartFile file) {
        pluginService.upload(file, id);
    }

    @ApiOperation("添加插件")
    @SaCheckPermission("iot:plugin:add")
    @PostMapping(value = "/add")
    @Log(title = "插件", businessType = BusinessType.INSERT)
    public void add(@Validated(AddGroup.class) @RequestBody Request<PluginInfoBo> request) {
        pluginService.addPlugin(request.getData());
    }

    @ApiOperation(value = "修改插件")
    @SaCheckPermission("iot:plugin:edit")
    @PostMapping("/edit")
    @Log(title = "插件", businessType = BusinessType.UPDATE)
    public void edit(@Validated(EditGroup.class) @RequestBody Request<PluginInfoBo> request) {
        pluginService.modifyPlugin(request.getData());
    }

    @ApiOperation(value = "插件详情")
    @SaCheckPermission("iot:plugin:list")
    @PostMapping("/detail")
    public PluginInfoVo detail(@RequestBody Request<Long> request) {
        return pluginService.getPlugin(request.getData());
    }

    @ApiOperation(value = "删除插件")
    @SaCheckPermission("iot:plugin:delete")
    @PostMapping("/delete")
    @Log(title = "插件", businessType = BusinessType.DELETE)
    public void delete(@Validated(EditGroup.class) @RequestBody Request<Long> request) {
        pluginService.deletePlugin(request.getData());
    }

    @ApiOperation("获取插件列表")
    @SaCheckPermission("monitor:plugin:list")
    @PostMapping("/list")
    public Paging<PluginInfoVo> list(@RequestBody @Validated(QueryGroup.class) PageRequest<PluginInfoBo> query) {
        return pluginService.findPagePluginList(query);
    }

    @ApiOperation("修改插件状态")
    @SaCheckPermission("monitor:plugin:edit")
    @PostMapping("/changeState")
    public void changeState(@RequestBody Request<PluginInfoBo> request) {
        pluginService.changeState(request.getData());
    }

}
