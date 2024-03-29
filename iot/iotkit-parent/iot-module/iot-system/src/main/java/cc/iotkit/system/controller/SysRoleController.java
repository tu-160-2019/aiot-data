package cc.iotkit.system.controller;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.api.Request;
import cc.iotkit.common.excel.utils.ExcelUtil;
import cc.iotkit.common.log.annotation.Log;
import cc.iotkit.common.log.enums.BusinessType;
import cc.iotkit.common.web.core.BaseController;
import cc.iotkit.model.system.SysUserRole;
import cc.iotkit.system.dto.bo.SysDeptBo;
import cc.iotkit.system.dto.bo.SysRoleAuthBo;
import cc.iotkit.system.dto.bo.SysRoleBo;
import cc.iotkit.system.dto.bo.SysUserBo;
import cc.iotkit.system.dto.vo.DeptTreeSelectVo;
import cc.iotkit.system.dto.vo.SysRoleVo;
import cc.iotkit.system.dto.vo.SysUserVo;
import cc.iotkit.system.service.ISysDeptService;
import cc.iotkit.system.service.ISysRoleService;
import cc.iotkit.system.service.ISysUserService;
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
 * 角色信息
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/role")
public class SysRoleController extends BaseController {

    private final ISysRoleService roleService;
    private final ISysUserService userService;
    private final ISysDeptService deptService;

    /**
     * 获取角色信息列表
     */
    @ApiOperation(value = "获取角色信息列表", notes = "获取角色信息列表,根据查询条件分页")
    @SaCheckPermission("system:role:list")
    @PostMapping("/list")
    public Paging<SysRoleVo> list(@RequestBody @Validated PageRequest<SysRoleBo> query) {
        return roleService.selectPageRoleList(query);
    }

    /**
     * 导出角色信息列表
     */
    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    @ApiOperation(value = "导出角色信息列表", notes = "导出角色信息列表")
    @SaCheckPermission("system:role:export")
    @PostMapping("/export")
    public void export(@Validated SysRoleBo role, HttpServletResponse response) {
        List<SysRoleVo> list = roleService.selectRoleList(role);
        ExcelUtil.exportExcel(list, "角色数据", SysRoleVo.class, response);
    }

    /**
     * 根据角色编号获取详细信息
     */
    @ApiOperation(value = "根据角色编号获取详细信息", notes = "根据角色编号获取详细信息")
    @SaCheckPermission("system:role:query")
    @PostMapping(value = "/getInfo")
    public SysRoleVo getInfo(@Validated @RequestBody Request<Long> bo) {
        Long roleId = bo.getData();
        roleService.checkRoleDataScope(roleId);
        return roleService.selectRoleById(roleId);
    }

    /**
     * 新增角色
     */
    @ApiOperation(value = "新增角色", notes = "新增角色")
    @SaCheckPermission("system:role:add")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public void add(@Validated @RequestBody Request<SysRoleBo> bo) {
        SysRoleBo role = bo.getData();

        if (!roleService.checkRoleNameUnique(role)) {
            fail("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (!roleService.checkRoleKeyUnique(role)) {
            fail("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        roleService.insertRole(role);

    }

    /**
     * 修改保存角色
     */
    @ApiOperation(value = "修改保存角色", notes = "修改保存角色")
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public void edit(@Validated @RequestBody Request<SysRoleBo> bo) {
        SysRoleBo role = bo.getData();

        roleService.checkRoleAllowed(role.getId());
        roleService.checkRoleDataScope(role.getId());
        if (!roleService.checkRoleNameUnique(role)) {
            fail("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        } else if (!roleService.checkRoleKeyUnique(role)) {
            fail("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }

        if (roleService.updateRole(role) > 0) {
            roleService.cleanOnlineUserByRole(role.getId());
            return;
        }
        fail("修改角色'" + role.getRoleName() + "'失败，请联系管理员");
    }

    /**
     * 修改保存数据权限
     */
    @ApiOperation(value = "修改保存数据权限", notes = "修改保存数据权限")
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PostMapping("/dataScope")
    public void dataScope(@RequestBody Request<SysRoleBo> bo) {
        SysRoleBo role = bo.getData();
        roleService.checkRoleAllowed(role.getId());
        roleService.checkRoleDataScope(role.getId());
        roleService.authDataScope(role);
    }

    /**
     * 状态修改
     */
    @ApiOperation(value = "状态修改", notes = "状态修改")
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public void changeStatus(@RequestBody Request<SysRoleBo> bo) {
        SysRoleBo role = bo.getData();
        roleService.checkRoleAllowed(role.getId());
        roleService.checkRoleDataScope(role.getId());
        roleService.updateRoleStatus(role.getId(), role.getStatus());
    }

    /**
     * 删除角色
     */
    @ApiOperation(value = "删除角色", notes = "删除角色")
    @SaCheckPermission("system:role:remove")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public void remove(@Validated @RequestBody Request<List<Long>> bo) {
        roleService.deleteRoleByIds(bo.getData());
    }

    /**
     * 获取角色选择框列表
     */
    @ApiOperation(value = "获取角色选择框列表", notes = "获取角色选择框列表")
    @SaCheckPermission("system:role:query")
    @PostMapping("/optionselect")
    public List<SysRoleVo> optionselect() {
        return roleService.selectRoleAll();
    }

    /**
     * 查询已分配用户角色列表
     */
    @ApiOperation(value = "查询已分配用户角色列表", notes = "查询已分配用户角色列表")
    @SaCheckPermission("system:role:list")
    @PostMapping("/authUser/allocatedList")
    public Paging<SysUserVo> allocatedList(@RequestBody @Validated PageRequest<SysUserBo> query) {
        return userService.selectAllocatedList(query);
    }

    /**
     * 查询未分配用户角色列表
     */
    @ApiOperation(value = "查询未分配用户角色列表", notes = "查询未分配用户角色列表")
    @SaCheckPermission("system:role:list")
    @PostMapping("/authUser/unallocatedList")
    public Paging<SysUserVo> unallocatedList(PageRequest<SysUserBo> query) {
        return userService.selectUnallocatedList(query);
    }

    /**
     * 取消授权用户
     */
    @ApiOperation(value = "取消授权用户", notes = "取消授权用户")
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PostMapping("/authUser/cancel")
    public void cancelAuthUser(@RequestBody Request<SysUserRole> bo) {
        roleService.deleteAuthUser(bo.getData());
    }

    /**
     * 批量取消授权用户
     *
     */
    @ApiOperation(value = "批量取消授权用户", notes = "批量取消授权用户")
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PostMapping("/authUser/cancelAll")
    public void cancelAuthUserAll(@Validated @RequestBody Request<SysRoleAuthBo> bo) {
        SysRoleAuthBo data = bo.getData();
        Long roleId = data.getRoleId();
        Long[] userIds = data.getUserIds();
        roleService.deleteAuthUsers(roleId, userIds);
    }

    /**
     * 批量选择用户授权
     *
     */
    @ApiOperation(value = "批量选择用户授权", notes = "批量选择用户授权")
    @SaCheckPermission("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PostMapping("/authUser/selectAll")
    public void selectAuthUserAll(@Validated @RequestBody Request<SysRoleAuthBo> bo) {
        SysRoleAuthBo data = bo.getData();
        Long roleId = data.getRoleId();
        Long[] userIds = data.getUserIds();
        roleService.checkRoleDataScope(roleId);
        roleService.insertAuthUsers(roleId, userIds);
    }

    /**
     * 获取对应角色部门树列表
     *
     */
    @ApiOperation(value = "获取对应角色部门树列表", notes = "获取对应角色部门树列表")
    @SaCheckPermission("system:role:list")
    @PostMapping(value = "/deptTreeByRoleId")
    public DeptTreeSelectVo roleDeptTreeselect(@Validated @RequestBody Request<Long> bo) {
        Long roleId = bo.getData();
        DeptTreeSelectVo selectVo = new DeptTreeSelectVo();
        selectVo.setCheckedKeys(deptService.selectDeptListByRoleId(roleId));
        selectVo.setDepts(deptService.selectDeptTreeList(new SysDeptBo()));
        return selectVo;
    }
}
