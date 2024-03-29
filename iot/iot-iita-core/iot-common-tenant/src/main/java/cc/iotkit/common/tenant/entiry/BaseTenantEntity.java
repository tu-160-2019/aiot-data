package cc.iotkit.common.tenant.entiry;

import cc.iotkit.common.tenant.dao.TenantAware;
import cc.iotkit.common.tenant.listener.TenantListener;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

/**
 * 类描述...
 *
 * @author Tiger Chen
 * created on 2023/7/15 20:53
 */


@MappedSuperclass
@Data
@NoArgsConstructor
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "tenantId", type = "string")})
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@EntityListeners(TenantListener.class)
public abstract class BaseTenantEntity implements TenantAware, Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column(name = "tenant_id")
    private String tenantId;

    /**
     * 创建部门
     */
    private Long createDept;

    /**
     * 创建者
     */
    @CreatedBy
    @Column(name = "create_by", updatable = false)
    private Long createBy;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", updatable = false)
    private Date createTime;

    /**
     * 更新者
     */
    @LastModifiedBy
    @Column(name = "update_by")
    private Long updateBy;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = "update_time")
    private Date updateTime;

    public BaseTenantEntity(String tenantId) {
        this.tenantId = tenantId;
    }

}