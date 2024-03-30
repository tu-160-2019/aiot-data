package cc.iotkit.system.controller;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.api.Request;
import cc.iotkit.common.excel.utils.ExcelUtil;
import cc.iotkit.common.log.annotation.Log;
import cc.iotkit.common.log.enums.BusinessType;
import cc.iotkit.common.validate.QueryGroup;
import cc.iotkit.common.web.core.BaseController;
import cc.iotkit.system.dto.bo.SysOperLogBo;
import cc.iotkit.system.dto.vo.SysOperLogVo;
import cc.iotkit.system.service.ISysOperLogService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.Api;
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
 * @Author：tfd
 * @Date：2023/5/31 15:08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/monitor/operlog")
@Api(tags = "操作日志记录")
public class SysOperlogController extends BaseController {

    private final ISysOperLogService operLogService;

    /**
     * 获取操作日志记录列表
     */
    @ApiOperation("获取操作日志记录列表")
    @SaCheckPermission("monitor:operlog:list")
    @PostMapping("/list")
    public Paging<SysOperLogVo> list(@RequestBody @Validated(QueryGroup.class) PageRequest<SysOperLogBo> query) {
        return operLogService.selectPageOperLogList(query);
    }

    /**
     * 导出操作日志记录列表
     */
    @ApiOperation("导出操作日志记录列表")
    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @SaCheckPermission("monitor:operlog:export")
    @PostMapping("/export")
    public void export(SysOperLogBo operLog, HttpServletResponse response) {
        List<SysOperLogVo> list = operLogService.selectOperLogList(operLog);
        ExcelUtil.exportExcel(list, "操作日志", SysOperLogVo.class, response);
    }

    /**
     * 批量删除操作日志记录
     */
    @ApiOperation("批量删除操作日志记录")
    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @SaCheckPermission("monitor:operlog:remove")
    @PostMapping("/delete")
    public void remove(@Validated @RequestBody Request<List<Long>> bo) {
        List<Long> operIds = bo.getData();
        operLogService.deleteOperLogByIds(operIds);
    }

    /**
     * 清理操作日志记录
     */
    @ApiOperation("清理操作日志记录")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @SaCheckPermission("monitor:operlog:remove")
    @PostMapping("/clean")
    public void clean() {
        operLogService.cleanOperLog();
    }
}
