package cc.iotkit.system.controller;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.api.Request;
import cc.iotkit.common.excel.core.ExcelResult;
import cc.iotkit.common.excel.utils.ExcelUtil;
import cc.iotkit.common.log.annotation.Log;
import cc.iotkit.common.log.enums.BusinessType;
import cc.iotkit.common.model.LoginUser;
import cc.iotkit.common.satoken.utils.LoginHelper;
import cc.iotkit.common.tenant.helper.TenantHelper;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.utils.StreamUtils;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.common.validate.EditGroup;
import cc.iotkit.common.validate.QueryGroup;
import cc.iotkit.common.web.core.BaseController;
import cc.iotkit.system.dto.bo.SysDeptBo;
import cc.iotkit.system.dto.bo.SysUserBo;
import cc.iotkit.system.dto.bo.SysUserRolesBo;
import cc.iotkit.system.dto.vo.*;
import cc.iotkit.system.listener.SysUserImportListener;
import cc.iotkit.system.service.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.secure.BCrypt;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户信息
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/user")
@Api(tags = "用户信息")
public class SysUserController extends BaseController {

    private final ISysUserService userService;
    private final ISysRoleService roleService;
    private final ISysPostService postService;
    private final ISysDeptService deptService;
    private final ISysTenantService tenantService;

    @ApiOperation("获取用户列表")
    @SaCheckPermission("system:user:list")
    @PostMapping("/list")
    public Paging<SysUserVo> list(@RequestBody @Validated(QueryGroup.class) PageRequest<SysUserBo> query) {
        return userService.selectPageUserList(query);
    }

    @ApiOperation("导出用户列表")
    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
    @SaCheckPermission("system:user:export")
    @PostMapping("/export")
    public void export(@Validated(QueryGroup.class) SysUserBo user,
                       HttpServletResponse response) {
        List<SysUserVo> list = userService.selectUserList(user);
        List<SysUserExportVo> listVo = MapstructUtils.convert(list, SysUserExportVo.class);
        ExcelUtil.exportExcel(listVo, "用户数据", SysUserExportVo.class, response);
    }

    /**
     * 导入数据
     *
     * @param file          导入文件
     * @param updateSupport 是否更新已存在数据
     */
    @ApiOperation("导入数据")
    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
    @SaCheckPermission("system:user:import")
    @PostMapping(value = "/importData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void importData(@RequestPart("file") MultipartFile file, boolean updateSupport) throws Exception {
        ExcelResult<SysUserImportVo> result = ExcelUtil.importExcel(file.getInputStream(), SysUserImportVo.class, new SysUserImportListener(updateSupport));
        result.getAnalysis();
    }

    /**
     * 获取导入模板
     */
    @ApiOperation("获取导入模板")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil.exportExcel(new ArrayList<>(), "用户数据", SysUserImportVo.class, response);
    }

    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @ApiOperation("获取用户信息")
    @PostMapping("/getInfo")
    public UserInfoVo getInfo() {
        UserInfoVo userInfoVo = new UserInfoVo();
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (LoginHelper.isSuperAdmin()) {
            // 超级管理员 如果重新加载用户信息需清除动态租户
            TenantHelper.clearDynamic();
        }
        SysUserVo user = userService.selectUserById(loginUser.getUserId());
        userInfoVo.setUser(user);
        userInfoVo.setPermissions(loginUser.getMenuPermission());
        userInfoVo.setRoles(loginUser.getRolePermission());
        return userInfoVo;
    }

    /**
     * 根据用户编号获取详细信息
     * 用户ID
     */
    @ApiOperation("根据用户编号获取详细信息")
    @SaCheckPermission("system:user:query")
    @PostMapping(value = {"/getDetail"})
    public SysUserInfoVo getInfo(@Validated @RequestBody Request<Long> req) {
        Long userId = req.getData();
        userService.checkUserDataScope(userId);
        SysUserInfoVo userInfoVo = new SysUserInfoVo();
        List<SysRoleVo> roles = roleService.selectRoleAll();
        userInfoVo.setRoles(LoginHelper.isSuperAdmin(userId) ? roles : StreamUtils.filter(roles, r -> !r.isSuperAdmin()));
        userInfoVo.setPosts(postService.selectPostAll());
        if (ObjectUtil.isNotNull(userId)) {
            SysUserVo sysUser = userService.selectUserById(userId);
            userInfoVo.setUser(sysUser);
            userInfoVo.setRoleIds(StreamUtils.toList(sysUser.getRoles(), SysRoleVo::getId));
            userInfoVo.setPostIds(postService.selectPostListByUserId(userId));
        }
        return userInfoVo;
    }

    /**
     * 新增用户
     */
    @ApiOperation("新增用户")
    @SaCheckPermission("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public void add(@Validated(EditGroup.class) @RequestBody Request<SysUserBo> reqUser) {
        SysUserBo user = reqUser.getData();
        if (!userService.checkUserNameUnique(user)) {
            fail("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            fail("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            fail("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        if (!tenantService.checkAccountBalance(TenantHelper.getTenantId())) {
            fail("当前租户下用户名额不足，请联系管理员");
        }
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        user.setUserType("sys_user");
        userService.insertUser(user);
    }

    /**
     * 修改用户
     */
    @ApiOperation("修改用户")
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public void edit(@Validated(EditGroup.class) @RequestBody Request<SysUserBo> reqUser) {
        SysUserBo user = reqUser.getData();
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getId());
        if (!userService.checkUserNameUnique(user)) {
            fail("修改用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(user)) {
            fail("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(user)) {
            fail("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        userService.updateUser(user);
    }

    /**
     * 删除用户
     */
    @ApiOperation("删除用户")
    @SaCheckPermission("system:user:remove")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public void remove(@Validated @RequestBody Request<List<Long>> bo) {
        List<Long> userIds = bo.getData();
        if (userIds.contains(LoginHelper.getUserId())) {
            fail("当前用户不能删除");
        }
        userService.deleteUserByIds(userIds);
    }

    /**
     * 重置密码
     */
    @ApiOperation("重置密码")
    @SaCheckPermission("system:user:resetPwd")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/resetPwd")
    public void resetPwd(@RequestBody @Validated(EditGroup.class) Request<SysUserBo> reqUser) {
        SysUserBo user = reqUser.getData();
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getId());
        user.setPassword(BCrypt.hashpw(user.getPassword()));
        userService.resetUserPwd(user.getId(), user.getPassword());
    }

    /**
     * 状态修改
     */
    @ApiOperation("状态修改")
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/changeStatus")
    public void changeStatus(@RequestBody @Validated(EditGroup.class) Request<SysUserBo> reqUser) {
        SysUserBo user = reqUser.getData();
        userService.checkUserAllowed(user);
        userService.checkUserDataScope(user.getId());
        userService.updateUserStatus(user.getId(), user.getStatus());
    }

    /**
     * 根据用户编号获取授权角色
     */
    @ApiOperation("根据用户编号获取授权角色")
    @SaCheckPermission("system:user:query")
    @PostMapping("/authRoleByUserId")
    public SysUserInfoVo authRole(@Validated @RequestBody Request<Long> bo) {
        Long userId = bo.getData();
        SysUserVo user = userService.selectUserById(userId);
        List<SysRoleVo> roles = roleService.selectRolesByUserId(userId);
        SysUserInfoVo userInfoVo = new SysUserInfoVo();
        userInfoVo.setUser(user);
        userInfoVo.setRoles(LoginHelper.isSuperAdmin(userId) ? roles : StreamUtils.filter(roles, r -> !r.isSuperAdmin()));
        return userInfoVo;
    }

    /**
     * 用户授权角色
     *
     * @param userRole 用户角色
     */
    @ApiOperation("用户授权角色")
    @SaCheckPermission("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PostMapping("/authRole")
    public void insertAuthRole(@RequestBody @Validated Request<SysUserRolesBo> userRole) {
        SysUserRolesBo data = userRole.getData();
        Long userId = data.getUserId();
        userService.checkUserDataScope(userId);
        userService.insertUserAuth(userId, data.getRoleIds());
    }

    /**
     * 获取部门树列表
     */
    @ApiOperation("获取部门树列表")
    @SaCheckPermission("system:user:list")
    @PostMapping("/deptTree")
    public List<Tree<Long>> deptTree(@RequestBody @Validated(QueryGroup.class) Request<SysDeptBo> reqDept) {
        return deptService.selectDeptTreeList(reqDept.getData());
    }

}
