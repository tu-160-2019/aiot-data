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

import cc.iotkit.data.model.TbHome;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HomeRepository extends JpaRepository<TbHome, Long> {

    TbHome findByUserIdAndCurrent(Long userId, boolean current);

    List<TbHome> findByUserId(Long userId);

    Page<TbHome> findByUserId(Long userId, Pageable pageable);

    long countByUserId(Long userId);

    void deleteById(Long s);

    long count();

    List<TbHome> findAll();

}
