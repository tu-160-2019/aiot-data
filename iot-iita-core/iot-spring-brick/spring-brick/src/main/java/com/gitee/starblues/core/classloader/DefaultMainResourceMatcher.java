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

import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.UrlUtils;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.Collection;
import java.util.Set;

/**
 * 默认的主程序资源匹配者
 *
 * @author starBlues
 * @since 3.0.0
 * @version 3.0.3
 */
public class DefaultMainResourceMatcher implements MainResourceMatcher{

    private final Set<String> includePatterns;
    private final Set<String> excludePatterns;

    private final PathMatcher pathMatcher;

    public DefaultMainResourceMatcher(MainResourcePatternDefiner mainResourcePatternDefiner) {
        this.includePatterns = mainResourcePatternDefiner.getIncludePatterns();
        this.excludePatterns = mainResourcePatternDefiner.getExcludePatterns();
        this.pathMatcher = new AntPathMatcher();
    }

    @Override
    public Boolean match(String resourceUrl) {
        return match(includePatterns, resourceUrl);
    }

    private Boolean match(Collection<String> patterns, String url){
        if(ObjectUtils.isEmpty(patterns) || ObjectUtils.isEmpty(url)){
            return Boolean.FALSE;
        }
        url = UrlUtils.formatMatchUrl(url);
        for (String pattern : patterns) {
            boolean match = pathMatcher.match(pattern, url);
            if(match){
                return !excludeMatch(excludePatterns, url);
            }
        }
        return Boolean.FALSE;
    }

    private Boolean excludeMatch(Collection<String> patterns, String url){
        if(ObjectUtils.isEmpty(patterns) || ObjectUtils.isEmpty(url)){
            return Boolean.FALSE;
        }
        url = UrlUtils.formatMatchUrl(url);
        for (String pattern : patterns) {
            boolean match = pathMatcher.match(pattern, url);
            if(match){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }



}
