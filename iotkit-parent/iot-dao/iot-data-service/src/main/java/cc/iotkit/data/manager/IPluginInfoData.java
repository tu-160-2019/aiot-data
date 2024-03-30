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
import cc.iotkit.model.plugin.PluginInfo;

/**
 * 插件信息接口
 *
 * @author sjg
 */
public interface IPluginInfoData extends ICommonData<PluginInfo, Long> {

    /**
     * 按插件包id取插件信息
     *
     * @param pluginId 插件包id
     * @return 插件信息
     */
    PluginInfo findByPluginId(String pluginId);

}
