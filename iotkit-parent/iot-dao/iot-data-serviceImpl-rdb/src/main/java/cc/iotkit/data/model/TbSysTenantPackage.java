package cc.iotkit.data.model;

import cc.iotkit.common.constant.UserConstants;
import cc.iotkit.model.system.SysTenantPackage;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * 租户套餐对象 sys_tenant_package
 *
 * @author Michelle.Chung
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_tenant_package")
@AutoMapper(target = SysTenantPackage.class)
public class TbSysTenantPackage extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 租户套餐id
     */
    @Id
    @GeneratedValue(generator = "SnowflakeIdGenerator")
    @GenericGenerator(name = "SnowflakeIdGenerator", strategy = "cc.iotkit.data.config.id.SnowflakeIdGenerator")
    @ApiModelProperty(value = "租户套餐id")
    private Long id;
    /**
     * 套餐名称
     */
    @ApiModelProperty(value = "套餐名称")
    private String packageName;
    /**
     * 关联菜单id
     */
    @Column(columnDefinition = "text")
    @ApiModelProperty(value = "关联菜单id")
    private String menuIds;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;
    /**
     * 菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）
     */
    @ApiModelProperty(value = "菜单树选择项是否关联显示（ 0：父子不互相关联显示 1：父子互相关联显示）")
    private Boolean menuCheckStrictly;
    /**
     * 状态（0正常 1停用）
     */
    @ApiModelProperty(value = "状态（0正常 1停用）")
    private String status;
    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    private String delFlag= UserConstants.NORMAL;

}
