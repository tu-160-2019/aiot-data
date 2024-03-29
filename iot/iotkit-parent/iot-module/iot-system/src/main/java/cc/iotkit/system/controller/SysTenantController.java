package cc.iotkit.system.controller;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.api.Request;
import cc.iotkit.common.constant.TenantConstants;
import cc.iotkit.common.excel.utils.ExcelUtil;
import cc.iotkit.common.log.annotation.Log;
import cc.iotkit.common.log.enums.BusinessType;
import cc.iotkit.common.tenant.helper.TenantHelper;
import cc.iotkit.common.validate.AddGroup;
import cc.iotkit.common.validate.EditGroup;
import cc.iotkit.common.web.core.BaseController;
import cc.iotkit.system.dto.bo.SysTenantBo;
import cc.iotkit.system.dto.vo.SysTenantVo;
import cc.iotkit.system.service.ISysTenantService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 租户管理
 *
 * @author Michelle.Chung
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/tenant")
@Api(tags = "租户管理")
public class SysTenantController extends BaseController {

    private final ISysTenantService tenantService;

    /**
     * 查询租户列表
     */
    @ApiOperation("查询租户列表")
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:list")
    @PostMapping("/list")
    public Paging<SysTenantVo> list(@Validated @RequestBody PageRequest<SysTenantBo> query) {
        return tenantService.queryPageList(query);
    }

    /**
     * 导出租户列表
     */
    @ApiOperation("导出租户列表")
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:export")
    @Log(title = "租户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysTenantBo bo, HttpServletResponse response) {
        List<SysTenantVo> list = tenantService.queryList(bo);
        ExcelUtil.exportExcel(list, "租户", SysTenantVo.class, response);
    }

    /**
     * 获取租户详细信息
     */
    @ApiOperation("获取租户详细信息")
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:query")
    @PostMapping("/getDetail")
    public SysTenantVo getInfo(@Validated @RequestBody Request<Long> bo) {
        return tenantService.queryById(bo.getData());
    }

    /**
     * 新增租户
     */
    @ApiOperation("新增租户")
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:add")
    @Log(title = "租户", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public void add(@Validated(AddGroup.class) @RequestBody Request<SysTenantBo> bo) {
        SysTenantBo data = bo.getData();
        if (!tenantService.checkCompanyNameUnique(data)) {
            fail("新增租户'" + data.getCompanyName() + "'失败，企业名称已存在");
        }
        tenantService.insertByBo(data);
    }

    /**
     * 修改租户
     */
    @ApiOperation("修改租户")
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:edit")
    @Log(title = "租户", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public void edit(@Validated(EditGroup.class) @RequestBody Request<SysTenantBo> bo) {
        SysTenantBo data = bo.getData();
        tenantService.checkTenantAllowed(data.getTenantId());
        if (!tenantService.checkCompanyNameUnique(data)) {
            fail("修改租户'" + data.getCompanyName() + "'失败，公司名称已存在");
        }
        tenantService.updateByBo(data);
    }

    /**
     * 状态修改
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:edit")
    @Log(title = "租户", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public void changeStatus(@RequestBody Request<SysTenantBo> bo) {
        SysTenantBo data = bo.getData();
        tenantService.checkTenantAllowed(data.getTenantId());
        tenantService.updateTenantStatus(data);
    }

    /**
     * 删除租户
     */
    @ApiOperation("删除租户")
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:remove")
    @Log(title = "租户", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public void remove(@Validated @RequestBody Request<Long> bo) {
        tenantService.deleteById(bo.getData());
    }

    /**
     * 动态切换租户
     */
    @ApiOperation("动态切换租户")
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @PostMapping("/dynamic")
    public void dynamicTenant(@Validated @RequestBody Request<String> bo) {
        TenantHelper.setDynamic(bo.getData());
    }

    /**
     * 清除动态租户
     */
    @ApiOperation("清除动态租户")
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @PostMapping("/dynamic/clear")
    public void dynamicClear() {
        TenantHelper.clearDynamic();
    }


    /**
     * 同步租户套餐
     *
     * @param tenantId  租户id
     * @param packageId 套餐id
     */
    @ApiOperation("同步租户套餐")
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenant:edit")
    @Log(title = "租户", businessType = BusinessType.UPDATE)
    @PostMapping("/syncTenantPackage")
    public void syncTenantPackage(@NotBlank(message = "租户ID不能为空") String tenantId, @NotBlank(message = "套餐ID不能为空") String packageId) {
        //TenantHelper.ignore(() -> tenantService.syncTenantPackage(tenantId, packageId));
    }

}
