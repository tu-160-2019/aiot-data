package cc.iotkit.data.system;

import cc.iotkit.data.ICommonData;
import cc.iotkit.model.system.SysMenu;

import java.util.List;

/**
 * 菜单数据接口
 *
 * @author sjg
 */
public interface ISysMenuData extends ICommonData<SysMenu, Long> {

    /**
     * 根据用户查询系统菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu, Long userId, boolean isSuperAdmin);

    List<String> selectMenuPermsByUserId(Long userId);

    List<String> selectMenuPermsByRoleId(Long roleId);

    /**
     * 根据用户ID查询菜单
     *
     * @return 菜单列表
     */
    List<SysMenu> selectMenuTreeAll();

    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenuTreeByUserId(Long userId);

    boolean hasChildByMenuId(Long menuId);

    boolean checkMenuNameUnique(SysMenu menu);


}
