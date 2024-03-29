/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.model.space;

import cc.iotkit.model.Id;
import cc.iotkit.model.TenantModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Space extends TenantModel implements Id<Long>, Serializable {

    private Long id;

    /**
     * 关联家庭id
     */
    private Long homeId;

    /**
     * 空间名称
     */
    private String name;

    /**
     * 设备数量
     */
    private Integer deviceNum;
}
