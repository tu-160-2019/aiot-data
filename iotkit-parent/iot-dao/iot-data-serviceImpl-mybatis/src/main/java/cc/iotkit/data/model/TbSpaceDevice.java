/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.data.model;

import cc.iotkit.common.tenant.dao.TenantAware;
import cc.iotkit.common.tenant.listener.TenantListener;
import cc.iotkit.model.space.SpaceDevice;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;

@Data
@Entity
@Table(name = "space_device")
@ApiModel(value = "空间设备")
@AutoMapper(target = SpaceDevice.class)
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "tenantId", type = "string")})
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
@EntityListeners(TenantListener.class)
public class TbSpaceDevice extends BaseEntity implements TenantAware {

    @Id
    @GeneratedValue(generator = "SnowflakeIdGenerator")
    @GenericGenerator(name = "SnowflakeIdGenerator", strategy = "cc.iotkit.data.config.id.SnowflakeIdGenerator")
    private Long id;

    /**
     * 空间中的设备id
     */
    @ApiModelProperty(value = "空间中的设备id")
    private String deviceId;

    /**
     * 空间中的设备名称
     */
    @ApiModelProperty(value = "空间中的设备名称")
    private String name;

    /**
     * 所属家庭Id
     */
    @ApiModelProperty(value = "所属家庭Id")
    private Long homeId;

    /**
     * 空间id
     */
    @ApiModelProperty(value = "空间id")
    private Long spaceId;

    /**
     * 是否收藏
     */
    @ApiModelProperty(value = "是否收藏")
    private Boolean collect;

    /**
     * 租户编号
     */
    @ApiModelProperty(value = "租户编号")
    private String tenantId;

}
