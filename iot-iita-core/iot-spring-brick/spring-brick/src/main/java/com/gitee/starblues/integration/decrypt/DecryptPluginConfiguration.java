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

package com.gitee.starblues.integration.decrypt;

import lombok.Data;

import java.util.Map;

/**
 * 解密插件配置
 *
 * @author starBlues
 * @version 3.0.1
 */
@Data
public class DecryptPluginConfiguration {

    /**
     * 插件id
     */
    private String pluginId;

    /**
     * 对当前插件特定的配置, 可以覆盖 {@link DecryptConfiguration#getProps()}  配置
     */
    private Map<String, Object> props;


}
