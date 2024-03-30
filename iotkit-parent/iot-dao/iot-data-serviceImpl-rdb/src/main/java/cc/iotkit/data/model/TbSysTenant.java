package cc.iotkit.data.model;

import cc.iotkit.common.constant.UserConstants;
import cc.iotkit.common.tenant.dao.TenantAware;
import cc.iotkit.model.system.SysTenant;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 租户对象 sys_tenant
 *
 * @author Michelle.Chung
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_tenant")
@AutoMapper(target = SysTenant.class)
public class TbSysTenant extends BaseEntity implements TenantAware {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "SnowflakeIdGenerator")
    @GenericGenerator(name = "SnowflakeIdGenerator", strategy = "cc.iotkit.data.config.id.SnowflakeIdGenerator")
    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")
    private String tenantId;

    /**
     * 联系人
     */
    @ApiModelProperty(value = "联系人")
    private String contactUserName;

    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话")
    private String contactPhone;

    /**
     * 企业名称
     */
    @ApiModelProperty(value = "企业名称")
    private String companyName;

    /**
     * 统一社会信用代码
     */
    @ApiModelProperty(value = "统一社会信用代码")
    private String licenseNumber;

    /**
     * 地址
     */
    @ApiModelProperty(value = "地址")
    private String address;

    /**
     * 域名
     */
    @ApiModelProperty(value = "域名")
    private String domain;

    /**
     * 企业简介
     */
    @ApiModelProperty(value = "企业简介")
    private String intro;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 租户套餐编号
     */
    @ApiModelProperty(value = "租户套餐编号")
    private Long packageId;

    /**
     * 过期时间
     */
    @ApiModelProperty(value = "过期时间")
    private Date expireTime;

    /**
     * 用户数量（-1不限制）
     */
    @ApiModelProperty(value = "用户数量（-1不限制）")
    private Long accountCount;

    /**
     * 租户状态（0正常 1停用）
     */
    @ApiModelProperty(value = "租户状态（0正常 1停用）")
    private String status=UserConstants.DICT_ABNORMAL;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    private String delFlag= UserConstants.NORMAL;

}
