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

import cc.iotkit.data.model.TbThingModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThingModelRepository extends JpaRepository<TbThingModel, Long> {

    Optional<TbThingModel> findByProductKey(String productKey);
}
