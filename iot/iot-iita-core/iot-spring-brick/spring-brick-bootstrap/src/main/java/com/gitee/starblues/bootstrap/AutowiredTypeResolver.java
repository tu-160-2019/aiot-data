/**
 * Copyright [2019-Present] [starBlues]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitee.starblues.bootstrap;

import com.gitee.starblues.bootstrap.annotation.AutowiredType;
import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import com.gitee.starblues.bootstrap.realize.AutowiredTypeDefiner;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.UrlUtils;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.Collections;
import java.util.Set;

/**
 * 注入类型处理
 *
 * @author starBlues
 * @since 3.0.3
 * @version 3.1.0
 */
public interface AutowiredTypeResolver {

    /**
     * 通过 descriptor 获取注入类型
     * @param descriptor descriptor
     * @return AutowiredType.Type
     */
    AutowiredType.Type resolve(DependencyDescriptor descriptor);

}
