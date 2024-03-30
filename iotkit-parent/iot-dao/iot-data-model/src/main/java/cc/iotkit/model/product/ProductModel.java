/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.model.product;

import cc.iotkit.model.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductModel implements Id<String> {

    public static final String TYPE_JS = "JavaScript";
    public static final String TYPE_LUA = "LuaScript";

    public static final String STATE_DEV = "dev";
    public static final String STATE_PUBLISH = "publish";

    private String id;

    /**
     * 型号在所有产品中唯一
     */
    private String model;

    private String name;

    private String productKey;

    private String type;

    private String script;

    /**
     * 脚本状态，只有发布状态才生效
     */
    private String state;

    private Long modifyAt;

    public static String getDefaultModel(String pk) {
        return pk + "_default";
    }

}
