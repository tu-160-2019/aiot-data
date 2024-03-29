/**
 * Copyright [2019-Present] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gitee.starblues.utils;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import org.slf4j.Logger;

/**
 * 日志打印工具
 *
 * @author starBlues
 * @since 3.0.0
 * @version 3.1.2
 */
public class LogUtils {

    private LogUtils(){}

    public static void info(Logger logger, PluginDescriptor pluginDescriptor, String msg){
        logger.info("插件[{}]{}", MsgUtils.getPluginUnique(pluginDescriptor), msg);
    }

    public static String getMsg(PluginDescriptor pluginDescriptor, String msg, Object... args){
        return "插件[ " + MsgUtils.getPluginUnique(pluginDescriptor) + " ]" + String.format(msg, args);
    }

}
