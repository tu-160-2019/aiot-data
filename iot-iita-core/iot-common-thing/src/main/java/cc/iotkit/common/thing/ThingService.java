/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.common.thing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sjg
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThingService<T> {

    public static final String TYPE_PROPERTY = "property";
    public static final String TYPE_SERVICE = "service";

    public static final String TYPE_OTA = "ota";

    private String mid;

    private String productKey;

    private String deviceName;

    private String type;

    private String identifier;

    private T params;

}
