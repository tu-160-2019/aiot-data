package cc.iotkit.model.system;

import cc.iotkit.model.BaseModel;
import cc.iotkit.model.Id;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * 租户套餐视图对象 sys_tenant_package
 *
 * @author Michelle.Chung
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SysTenantPackage extends BaseModel implements Id<Long>, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 租户套餐id
     */
    private Long id;

    /**
     * 套餐名称
     */
    private String packageName;

    /**
     * 关联菜单id
     */
    private String menuIds;

    /**
     * 备注
     */
    private String remark;

    /**
     * 菜单树选择项是否关联显示
     */
    private Boolean menuCheckStrictly;

    /**
     * 状态（0正常 1停用）
     */
    private String status;


}
