/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.manager.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpaceDeviceVo {

    private Long id;

    /**
     * 空间中的设备id
     */
    private String deviceId;

    /**
     * 空间中的设备名称
     */
    private String name;

    /**
     * 设备DN
     */
    private String deviceName;

    /**
     * 设备图片
     */
    private String picUrl;

    /**
     * 空间ID
     */
    private Long spaceId;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 设备状态
     */
    private Boolean online;

    /**
     * 设备属性
     */
    private Map<String, Object> property = new HashMap<>();

    /**
     * 产品key
     */
    private String productKey;

    /**
     * 产品名
     */
    private String productName;

    /**
     * 品类
     */
    private String category;

    /**
     * 品类名
     */
    private String categoryName;

    /**
     * 是否收藏
     */
    private Boolean collect;
}
