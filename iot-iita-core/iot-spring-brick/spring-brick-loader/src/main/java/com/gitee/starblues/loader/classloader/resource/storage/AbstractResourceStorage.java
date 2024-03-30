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

package com.gitee.starblues.loader.classloader.resource.storage;

import com.gitee.starblues.loader.classloader.resource.Resource;
import com.gitee.starblues.loader.classloader.resource.loader.DefaultResource;
import com.gitee.starblues.loader.utils.IOUtils;
import com.gitee.starblues.loader.utils.ObjectUtils;
import com.gitee.starblues.loader.utils.ResourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * 抽象的资源存储
 *
 * @author starBlues
 * @since 3.0.0
 * @version 3.1.2
 */
public abstract class AbstractResourceStorage implements ResourceStorage {

    private final Set<URL> baseUrls = new HashSet<>();
    private final ArrayDeque<URL> hotUrls = new ArrayDeque<>();
    private final List<InputStream> inputStreams = new ArrayList<>();

    @Override
    public void addBaseUrl(URL url){
        if(url == null){
            return;
        }
        synchronized (baseUrls){
            baseUrls.add(url);
        }
    }

    @Override
    public List<URL> getBaseUrl(){
        synchronized (baseUrls){
            return Collections.unmodifiableList(new ArrayList<>(baseUrls));
        }
    }


    @Override
    public final void add(Resource resource) throws Exception {
        addResource(resource);
    }

    @Override
    public void close() throws Exception {
        for (InputStream inputStream : inputStreams) {
            IOUtils.closeQuietly(inputStream);
        }
        inputStreams.clear();
        hotUrls.clear();
        baseUrls.clear();
    }

    /**
     * 子类添加资源
     * @param resource 资源
     * @throws Exception 添加移除
     */
    protected abstract void addResource(Resource resource) throws Exception;

    /**
     * 格式化资源名称
     * @param name 资源名称
     * @return String
     */
    protected final String formatResourceName(String name) {
        return ResourceUtils.formatStandardName(name);
    }

    protected final InputStream openStream(Resource resource){
        if(resource == null){
            return null;
        }
        try {
            InputStream inputStream = resource.getUrl().openStream();
            inputStreams.add(inputStream);
            return inputStream;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected final Enumeration<InputStream> openStream(Enumeration<Resource> resources){
        if(resources == null){
            return Collections.emptyEnumeration();
        }
        return new Enumeration<InputStream>() {
            @Override
            public boolean hasMoreElements() {
                return resources.hasMoreElements();
            }

            @Override
            public InputStream nextElement() {
                Resource resource = resources.nextElement();
                return openStream(resource);
            }
        };
    }

    protected final void closeResources(List<Resource> resources){
        if(ObjectUtils.isEmpty(resources)){
            return;
        }
        for (Resource resource : resources) {
            IOUtils.closeQuietly(resource);
        }
        resources.clear();
    }

    protected final synchronized Resource searchResource(String name) {
        Set<URL> searchUrl = new HashSet<>();
        URL matchBaseUrl = null;
        URL matchExistUrl = null;
        // TODO 还需优化
        while (true){
            URL baseUrl = hotUrls.pollFirst();
            if(baseUrl == null){
                break;
            }
            searchUrl.add(baseUrl);
            URL existUrl = ResourceUtils.getExistUrl(baseUrl, name);
            if(existUrl != null){
                matchBaseUrl = baseUrl;
                matchExistUrl = existUrl;
                hotUrls.addFirst(baseUrl);
                break;
            }
        }

        if(matchBaseUrl == null){
            List<URL> baseUrls = getBaseUrl();
            for (URL baseUrl : baseUrls) {
                if(searchUrl.contains(baseUrl)){
                    continue;
                }
                URL existUrl = ResourceUtils.getExistUrl(baseUrl, name);
                if(existUrl != null){
                    matchBaseUrl = baseUrl;
                    matchExistUrl = existUrl;
                    hotUrls.addFirst(baseUrl);
                    break;
                }
            }
        }
        if(matchBaseUrl != null){
            try {
                Resource resource = new DefaultResource(name, matchBaseUrl, matchExistUrl);
                addResource(resource);
                return resource;
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    protected final Enumeration<Resource> searchResources(String name){
        List<URL> baseUrls = getBaseUrl();
        return new InternalEnumeration(baseUrls, name);
    }


    private class InternalEnumeration implements Enumeration<Resource>{

        private final List<URL> baseUrls;
        private final String name;

        private int index = 0;
        private Resource resource = null;

        private InternalEnumeration(List<URL> baseUrls, String name) {
            this.baseUrls = baseUrls;
            this.name = name;
        }

        @Override
        public boolean hasMoreElements() {
            return next();
        }
        @Override
        public Resource nextElement() {
            if (!next()) {
                throw new NoSuchElementException();
            }
            Resource r = resource;
            resource = null;
            return r;
        }

        private boolean next() {
            if (resource != null) {
                return true;
            } else {
                URL baseUrl;
                while (index < baseUrls.size()){
                    baseUrl = baseUrls.get(index++);
                    resource = getResource(baseUrl);
                    if(resource != null){
                        return true;
                    }
                }
                return false;
            }
        }

        private Resource getResource(URL baseUrl){
            URL existUrl = ResourceUtils.getExistUrl(baseUrl, name);
            if(existUrl == null){
                return null;
            }
            Resource resource = new DefaultResource(name, baseUrl, existUrl);
            addResourceWrapper(resource);
            return resource;
        }
    }

    private void addResourceWrapper(Resource resource){
        if(resource == null){
            return;
        }
        try {
            addResource(resource);
        } catch (Exception e){
            // 忽略异常
        }
    }

}
