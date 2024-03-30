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

import com.gitee.starblues.common.cipher.AesPluginCipher;
import com.gitee.starblues.common.cipher.RsaPluginCipher;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对插件启动时进行解密校验配置。默认为不启用
 *
 * @author starBlues
 * @version 3.0.1
 */
@Data
public class DecryptConfiguration {

    /**
     * 是否启动. 默认启用
     */
    private Boolean enable = true;

    /**
     * 加解密实现类名称.
     * 通过类加载器加载, 然后从Spring容器中获取, 获取不到, 对其直接实例化
     * 默认: {@link AesPluginCipher}
     * 可选:
     * @see com.gitee.starblues.common.cipher.AesPluginCipher
     * @see com.gitee.starblues.common.cipher.RsaPluginCipher
     */
    private String className = AesPluginCipher.class.getName();

    /**
     * 总配置
     */
    private Map<String, Object> props = new HashMap<>();

    /**
     * 指定可生效的插件. 如果不配置, 则默认对全部插件生效
     */
    private List<DecryptPluginConfiguration> plugins;

}
