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

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductRepository extends BaseMapper<TbProduct> {

    List<TbProduct> findByCategory(String category);

    List<TbProduct> findByUid(String uid);

    Page<TbProduct> findByUid(String uid, Pageable pageable);

    long countByUid(String uid);

    TbProduct findByProductKey(String productKey);
}
