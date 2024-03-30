/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<TbProduct, Long> {

    List<TbProduct> findByCategory(String category);

    List<TbProduct> findByUid(String uid);

    Page<TbProduct> findByUid(String uid, Pageable pageable);

    long countByUid(String uid);

    TbProduct findByProductKey(String productKey);
}
