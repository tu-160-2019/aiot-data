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

package com.gitee.starblues.core.classloader;

import com.gitee.starblues.loader.utils.IOUtils;
import com.gitee.starblues.utils.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 组合的 MainResourcePatternDefiner
 *
 * @author starBlues
 * @since 3.0.0
 * @version 3.0.3
 */
public class ComposeMainResourceMatcher implements MainResourceMatcher, AutoCloseable{

    private final List<MainResourceMatcher> resourceMatchers;

    public ComposeMainResourceMatcher(){
        this(null);
    }

    public ComposeMainResourceMatcher(List<MainResourceMatcher> resourceMatchers) {
        if(ObjectUtils.isEmpty(resourceMatchers)){
            this.resourceMatchers = new ArrayList<>();
        } else {
            this.resourceMatchers = resourceMatchers;
        }
    }

    public void addMainResourceMatcher(MainResourceMatcher mainResourceMatcher){
        if(mainResourceMatcher == null){
            return;
        }
        resourceMatchers.add(mainResourceMatcher);
    }

    @Override
    public Boolean match(String resourceUrl) {
        for (MainResourceMatcher resourceMatcher : resourceMatchers) {
            if(resourceMatcher.match(resourceUrl)){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public void close() throws Exception {
        for (MainResourceMatcher resourceMatcher : resourceMatchers) {
            if(resourceMatcher instanceof AutoCloseable){
                IOUtils.closeQuietly((AutoCloseable)resourceMatcher);
            }
        }
    }
}
