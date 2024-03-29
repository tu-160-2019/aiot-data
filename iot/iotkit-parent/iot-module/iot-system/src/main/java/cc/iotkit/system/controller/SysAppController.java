package cc.iotkit.system.controller;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.api.Request;
import cc.iotkit.common.excel.utils.ExcelUtil;
import cc.iotkit.common.log.annotation.Log;
import cc.iotkit.common.log.enums.BusinessType;
import cc.iotkit.common.validate.AddGroup;
import cc.iotkit.common.validate.EditGroup;
import cc.iotkit.common.web.core.BaseController;
import cc.iotkit.system.dto.bo.SysAppBo;
import cc.iotkit.system.dto.vo.SysAppVo;
import cc.iotkit.system.service.ISysAppService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 应用信息
 *
 * @author tfd
 * @date 2023-08-10
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/app")
public class SysAppController extends BaseController {

    private final ISysAppService sysAppService;

    /**
     * 查询应用信息列表
     */
    @SaCheckPermission("system:app:list")
    @PostMapping("/list")
    @ApiOperation("查询应用信息列表")
    public Paging<SysAppVo> list( PageRequest<SysAppBo> pageQuery) {
        return sysAppService.queryPageList(pageQuery);
    }

    /**
     * 导出应用信息列表
     */
    @ApiOperation("导出应用信息列表")
    @SaCheckPermission("system:app:export")
    @Log(title = "应用信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysAppBo bo, HttpServletResponse response) {
        List<SysAppVo> list = sysAppService.queryList(bo);
        ExcelUtil.exportExcel(list, "应用信息", SysAppVo.class, response);
    }

    /**
     * 获取应用信息详细信息
     *
     */
    @SaCheckPermission("system:app:query")
    @PostMapping("/getDetail")
    @ApiOperation("获取应用信息详细信息")
    public SysAppVo getDetail(@Validated @RequestBody Request<Long> request) {
        return sysAppService.queryById(request.getData());
    }

    /**
     * 新增应用信息
     */
    @SaCheckPermission("system:app:add")
    @Log(title = "应用信息", businessType = BusinessType.INSERT)
    @PostMapping(value = "/add")
    @ApiOperation("新增应用信息")
    public Long add(@Validated(AddGroup.class) @RequestBody Request<SysAppBo> request) {
        if (sysAppService.checkAppIdUnique(request.getData().getAppId())) {
            fail("新增应用'" + request.getData().getAppName() + "'失败，APPID已存在");
        }
        return sysAppService.insertByBo(request.getData());
    }

    /**
     * 修改应用信息
     */
    @SaCheckPermission("system:app:edit")
    @Log(title = "应用信息", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ApiOperation("修改应用信息")
    public boolean edit(@Validated(EditGroup.class) @RequestBody  Request<SysAppBo> request) {
        return sysAppService.updateByBo(request.getData());
    }

    /**
     * 删除应用信息
     *
     */
    @SaCheckPermission("system:app:remove")
    @Log(title = "应用信息", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    @ApiOperation("删除应用信息")
    public boolean remove(@Validated @RequestBody Request<Long> query) {
        return sysAppService.deleteById(query.getData());
    }
}
