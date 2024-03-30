/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.data.manager;

import cc.iotkit.data.ICommonData;
import cc.iotkit.model.product.Product;

import java.util.List;

/**
 * 产品接口
 */
public interface IProductData extends ICommonData<Product, Long> {

    /**
     * 按品类取产品列表
     */
    List<Product> findByCategory(String category);


    Product findByProductKey(String productKey);

    void delByProductKey(String productKey);
}
