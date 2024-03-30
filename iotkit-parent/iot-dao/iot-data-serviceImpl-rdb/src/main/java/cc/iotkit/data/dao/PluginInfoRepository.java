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

import cc.iotkit.data.model.TbPluginInfo;
import cc.iotkit.data.model.TbSysOperLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * @author sjg
 */
public interface PluginInfoRepository extends JpaRepository<TbPluginInfo, Long>, QuerydslPredicateExecutor<TbSysOperLog> {

    TbPluginInfo findByPluginId(String pluginId);

}
