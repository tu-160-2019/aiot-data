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
import cc.iotkit.data.model.TbPluginInstance;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author sjg
 */
@Mapper
public interface PluginInstanceRepository extends BaseMapper<TbPluginInstance> {

    /**
     * 按主程序id和插件id获取插件实例
     *
     * @param mainId   主程序id
     * @param pluginId 插件id
     * @return 插件实例
     */
    TbPluginInfo findByMainIdAndPluginId(String mainId, String pluginId);

}
