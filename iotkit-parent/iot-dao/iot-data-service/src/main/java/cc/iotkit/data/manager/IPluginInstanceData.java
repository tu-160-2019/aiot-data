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
import cc.iotkit.model.plugin.PluginInstance;

/**
 * 插件实例接口
 *
 * @author sjg
 */
public interface IPluginInstanceData extends ICommonData<PluginInstance, Long> {

    /**
     * 获取插件实例
     *
     * @param mainId   主程序id
     * @param pluginId 插件包id
     * @return 插件实例
     */
    PluginInstance findInstance(String mainId, String pluginId);

}
