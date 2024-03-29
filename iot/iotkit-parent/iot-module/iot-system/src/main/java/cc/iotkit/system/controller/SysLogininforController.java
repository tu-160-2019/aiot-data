package cc.iotkit.system.controller;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.api.Request;
import cc.iotkit.common.constant.GlobalConstants;
import cc.iotkit.common.excel.utils.ExcelUtil;
import cc.iotkit.common.log.annotation.Log;
import cc.iotkit.common.log.enums.BusinessType;
import cc.iotkit.common.redis.utils.RedisUtils;
import cc.iotkit.common.validate.QueryGroup;
import cc.iotkit.common.web.core.BaseController;
import cc.iotkit.system.dto.bo.SysLoginInfoBo;
import cc.iotkit.system.dto.vo.SysLogininforVo;
import cc.iotkit.system.service.ISysLogininforService;
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
 * @Date：2023/5/31 15:53
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/monitor/logininfor")
@Api(tags = "系统访问记录")
public class SysLogininforController extends BaseController {

    private final ISysLogininforService logininforService;

    /**
     * 获取系统访问记录列表
     */

    @ApiOperation("获取系统访问记录列表")
    @SaCheckPermission("monitor:logininfor:list")
    @PostMapping("/list")
    public Paging<SysLogininforVo> list(@RequestBody @Validated(QueryGroup.class) PageRequest<SysLoginInfoBo> query) {
        return logininforService.findAll(query);
    }

    /**
     * 导出系统访问记录列表
     */
    @ApiOperation("获取系统访问记录列表")
    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @SaCheckPermission("monitor:logininfor:export")
    @PostMapping("/export")
    public void export(SysLoginInfoBo logininfor, HttpServletResponse response) {
        List<SysLogininforVo> list = logininforService.selectLogininforList(logininfor);
        ExcelUtil.exportExcel(list, "登录日志", SysLogininforVo.class, response);
    }

    /**
     * 批量删除登录日志

     */
    @ApiOperation("批量删除登录日志")
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public void remove(@RequestBody @Validated Request<List<Long>> bo) {
        logininforService.deleteLogininforByIds(bo.getData());
    }

    /**
     * 清理系统访问记录
     */
    @ApiOperation("清理系统访问记录")
    @SaCheckPermission("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    @PostMapping("/clean")
    public void clean() {
        logininforService.cleanLogininfor();
    }

    @ApiOperation("账户解锁")
    @SaCheckPermission("monitor:logininfor:unlock")
    @Log(title = "账户解锁", businessType = BusinessType.OTHER)
    @PostMapping("/unlockByUserName")
    public void unlock(@Validated @RequestBody Request<String> bo) {
        String loginName = GlobalConstants.PWD_ERR_CNT_KEY + bo.getData();
        if (Boolean.TRUE.equals(RedisUtils.hasKey(loginName))) {
            RedisUtils.deleteObject(loginName);
        }
    }
}
