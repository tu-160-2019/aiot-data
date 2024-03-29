package cc.iotkit.system.dto;

import lombok.Data;

/**
 * 角色和菜单关联
 *
 * @author Lion Li
 */

@Data
public class SysRoleMenu {

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 菜单ID
     */
    private Long menuId;

}
