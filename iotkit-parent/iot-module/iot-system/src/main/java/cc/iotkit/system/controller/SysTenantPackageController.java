package cc.iotkit.system.controller;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.api.Request;
import cc.iotkit.common.constant.TenantConstants;
import cc.iotkit.common.excel.utils.ExcelUtil;
import cc.iotkit.common.log.annotation.Log;
import cc.iotkit.common.log.enums.BusinessType;
import cc.iotkit.common.validate.AddGroup;
import cc.iotkit.common.validate.EditGroup;
import cc.iotkit.common.web.core.BaseController;
import cc.iotkit.system.dto.bo.SysTenantPackageBo;
import cc.iotkit.system.dto.vo.SysTenantPackageVo;
import cc.iotkit.system.service.ISysTenantPackageService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 租户套餐管理
 *
 * @author Michelle.Chung
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/tenant/package")
public class SysTenantPackageController extends BaseController {

    private final ISysTenantPackageService tenantPackageService;

    /**
     * 查询租户套餐列表
     */
    @ApiOperation("查询租户套餐列表")
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:list")
    @PostMapping("/list")
    public Paging<SysTenantPackageVo> list(  PageRequest<SysTenantPackageBo> query) {
        return tenantPackageService.queryPageList( query);
    }

    /**
     * 查询租户套餐下拉选列表
     */
    @ApiOperation("查询租户套餐下拉选列表")
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:list")
    @PostMapping("/selectList")
    public List<SysTenantPackageVo> selectList() {
        return tenantPackageService.selectList();
    }

    /**
     * 导出租户套餐列表
     */
    @ApiOperation("导出租户套餐列表")
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:export")
    @Log(title = "租户套餐", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysTenantPackageBo bo, HttpServletResponse response) {
        List<SysTenantPackageVo> list = tenantPackageService.queryList(bo);
        ExcelUtil.exportExcel(list, "租户套餐", SysTenantPackageVo.class, response);
    }

    /**
     * 获取租户套餐详细信息
     *
     * @param packageId 主键
     */
    @ApiOperation("获取租户套餐详细信息")
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:query")
    @PostMapping("/getInfo")
    public SysTenantPackageVo getInfo(@NotNull(message = "主键不能为空")
                                      @PathVariable Long packageId) {
        return tenantPackageService.queryById(packageId);
    }

    /**
     * 新增租户套餐
     */
    @ApiOperation("新增租户套餐")
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:add")
    @Log(title = "租户套餐", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public void add(@Validated(AddGroup.class) @RequestBody Request<SysTenantPackageBo> bo) {
        tenantPackageService.insertByBo(bo.getData());
    }

    /**
     * 修改租户套餐
     */
    @ApiOperation("修改租户套餐")
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:edit")
    @Log(title = "租户套餐", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public void edit(@Validated(EditGroup.class) @RequestBody Request<SysTenantPackageBo> bo) {
        tenantPackageService.updateByBo(bo.getData());
    }

    /**
     * 状态修改
     */
    @ApiOperation("状态修改")
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:edit")
    @Log(title = "租户套餐", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public void changeStatus(@RequestBody Request<SysTenantPackageBo> bo) {
        tenantPackageService.updatePackageStatus(bo.getData());
    }

    /**
     * 删除租户套餐
     *
     * @param packageIds 主键串
     */
    @ApiOperation("删除租户套餐")
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:tenantPackage:remove")
    @Log(title = "租户套餐", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public void remove(@NotEmpty(message = "主键不能为空")
                       @PathVariable Long[] packageIds) {
        tenantPackageService.deleteWithValidByIds(List.of(packageIds), true);
    }
}
