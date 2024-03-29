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

/**
 * msg 工具
 *
 * @author starBlues
 * @since 3.0.0
 * @version 3.0.0
 */
public abstract class MsgUtils {

    private MsgUtils(){}

    public static String getPluginUnique(PluginDescriptor pluginDescriptor){
        return pluginDescriptor.getPluginId() + "@" + pluginDescriptor.getPluginVersion();
    }

    public static String getPluginUnique(String pluginId, String version){
        if(ObjectUtils.isEmpty(version)){
            return pluginId;
        }
        return pluginId + "@" + version;
    }

    public static String getThrowableMsg(Throwable throwable){
        return ObjectUtils.isEmpty(throwable.getMessage()) ? "" : throwable.getMessage();
    }

    public static String getThrowableMsg(String message){
        return ObjectUtils.isEmpty(message) ? "" : message;
    }

}
