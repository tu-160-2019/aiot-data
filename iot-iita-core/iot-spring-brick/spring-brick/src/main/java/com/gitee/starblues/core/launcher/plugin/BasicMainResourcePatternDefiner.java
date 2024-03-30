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

package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.classloader.MainResourcePatternDefiner;
import com.gitee.starblues.utils.Assert;
import com.gitee.starblues.utils.ObjectUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 基本的主程序资源匹配定义
 * @author starBlues
 * @since 3.0.0
 * @version 3.0.0
 */
public class BasicMainResourcePatternDefiner implements MainResourcePatternDefiner {

    private final String mainPackageName;

    public BasicMainResourcePatternDefiner(String mainPackageName) {
        this.mainPackageName = ObjectUtils.changePackageToMatch(mainPackageName);
    }

    @Override
    public Set<String> getIncludePatterns() {
        Set<String> includePatterns = new HashSet<>();
        includePatterns.add(mainPackageName);
        return includePatterns;
    }

    @Override
    public Set<String> getExcludePatterns() {
        return null;
    }
}
