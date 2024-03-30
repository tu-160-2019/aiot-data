/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.manager.dto.bo.space;

import cc.iotkit.common.api.BaseDto;
import cc.iotkit.model.space.Home;
import cc.iotkit.model.space.Space;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Home.class, reverseConvertGenerate = false)
public class HomeBo extends BaseDto {

    private Long id;

    /**
     * 家庭名称
     */
    private String name;

    /**
     * 家庭地址
     */
    private String address;

    /**
     * 关联用户id
     */
    private Long userId;

    /**
     * 空间数量
     */
    private Integer spaceNum;

    /**
     * 设备数量
     */
    private Integer deviceNum;

    /**
     * 是否为用户当前使用的家庭
     */
    private Boolean current;

    /**
     * 空间对象
     */
    private List<Space> spaces;
}
