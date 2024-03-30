package cc.iotkit.system.controller;

import cc.iotkit.common.api.Request;
import cc.iotkit.common.constant.TenantConstants;
import cc.iotkit.common.constant.UserConstants;
import cc.iotkit.common.log.annotation.Log;
import cc.iotkit.common.log.enums.BusinessType;
import cc.iotkit.common.satoken.utils.LoginHelper;
import cc.iotkit.common.utils.StringUtils;
import cc.iotkit.common.web.core.BaseController;
import cc.iotkit.model.system.SysMenu;
import cc.iotkit.system.dto.bo.SysMenuBo;
import cc.iotkit.system.dto.vo.MenuTreeSelectVo;
import cc.iotkit.system.dto.vo.RouterVo;
import cc.iotkit.system.dto.vo.SysMenuVo;
import cc.iotkit.system.service.ISysMenuService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.lang.tree.Tree;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜单信息
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/menu")
public class SysMenuController extends BaseController {

    private final ISysMenuService menuService;

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @ApiOperation("获取路由信息")
    @PostMapping("/getRouters")
    public List<RouterVo> getRouters() {
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(LoginHelper.getUserId());
        return menuService.buildMenus(menus);
    }

    /**
     * 获取菜单列表
     */
    @ApiOperation("获取菜单列表")
    @SaCheckRole(value = {
            TenantConstants.SUPER_ADMIN_ROLE_KEY,
            TenantConstants.TENANT_ADMIN_ROLE_KEY
    }, mode = SaMode.OR)
    @SaCheckPermission("system:menu:list")
    @PostMapping("/list")
    public List<SysMenuVo> list(@RequestBody Request<SysMenuBo> bo) {
        return menuService.selectMenuList(bo.getData(), LoginHelper.getUserId());
    }

    /**
     * 根据菜单编号获取详细信息
     *
     * @param menuId 菜单ID
     */
    @SaCheckRole(value = {
            TenantConstants.SUPER_ADMIN_ROLE_KEY,
            TenantConstants.TENANT_ADMIN_ROLE_KEY
    }, mode = SaMode.OR)
    @SaCheckPermission("system:menu:query")
    @PostMapping(value = "/getInfo")
    public SysMenuVo getInfo(@RequestBody Request<Long> menuId) {
        return menuService.selectMenuById(menuId.getData());
    }

    /**
     * 获取菜单下拉树列表
     */
    @ApiOperation("获取菜单下拉树列表")
    @SaCheckRole(value = {
            TenantConstants.SUPER_ADMIN_ROLE_KEY,
            TenantConstants.TENANT_ADMIN_ROLE_KEY
    }, mode = SaMode.OR)
    @SaCheckPermission("system:menu:query")
    @PostMapping("/treeselect")
    public List<Tree<Long>> treeSelect(@Validated @RequestBody  Request<SysMenuBo> menu) {
        List<SysMenuVo> menus = menuService.selectMenuList(menu.getData(), LoginHelper.getUserId());
        return menuService.buildMenuTreeSelect(menus);
    }

    /**
     * 加载对应角色菜单列表树
     *
     */
    @ApiOperation("加载对应角色菜单列表树")
    @SaCheckRole(value = {
            TenantConstants.SUPER_ADMIN_ROLE_KEY,
            TenantConstants.TENANT_ADMIN_ROLE_KEY
    }, mode = SaMode.OR)
    @SaCheckPermission("system:menu:query")
    @PostMapping(value = "/roleMenuTreeselectByRoleId")
    public MenuTreeSelectVo roleMenuTreeSelect(@Validated @RequestBody Request<Long> bo) {
        List<SysMenuVo> menus = menuService.selectMenuList(LoginHelper.getUserId());
        MenuTreeSelectVo selectVo = new MenuTreeSelectVo();
        selectVo.setCheckedKeys(menuService.selectMenuListByRoleId(bo.getData()));
        selectVo.setMenus(menuService.buildMenuTreeSelect(menus));
        return selectVo;
    }

    /**
     * 新增菜单
     */
    @ApiOperation("新增菜单")
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:menu:add")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public void add(@Validated @RequestBody Request<SysMenuBo> bo) {
        SysMenuBo menu = bo.getData();
        if (!menuService.checkMenuNameUnique(menu)) {
            fail("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath())) {
            fail("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        menuService.insertMenu(menu);
    }

    /**
     * 修改菜单
     */
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:menu:edit")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    public void edit(@Validated @RequestBody Request<SysMenuBo> bo) {
        SysMenuBo menu = bo.getData();
        if (!menuService.checkMenuNameUnique(menu)) {
            fail("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        } else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath())) {
            fail("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        } else if (menu.getId().equals(menu.getParentId())) {
            fail("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        menuService.updateMenu(menu);
    }

    /**
     * 删除菜单
     *
     */
    @ApiOperation("删除菜单")
    @SaCheckRole(TenantConstants.SUPER_ADMIN_ROLE_KEY)
    @SaCheckPermission("system:menu:remove")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public void remove(@Validated @RequestBody Request<Long> bo) {
        Long menuId = bo.getData();
        if (menuService.hasChildByMenuId(menuId)) {
            warn("存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId)) {
            warn("菜单已分配,不允许删除");
        }
        menuService.deleteMenuById(menuId);
    }

}
