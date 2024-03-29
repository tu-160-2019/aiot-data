/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.vertx;

import io.vertx.core.Vertx;

public class VertxManager {

    private static final Vertx INSTANCE = Vertx.vertx();

    public static Vertx getVertx() {
        return INSTANCE;
    }
}
