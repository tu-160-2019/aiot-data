/**
 * Copyright [2019-Present] [starBlues]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitee.starblues.loader.classloader;

import com.gitee.starblues.loader.classloader.resource.Resource;
import com.gitee.starblues.loader.classloader.resource.loader.ResourceLoader;
import com.gitee.starblues.loader.classloader.resource.loader.ResourceLoaderFactory;
import com.gitee.starblues.loader.utils.Assert;
import com.gitee.starblues.loader.utils.IOUtils;
import com.gitee.starblues.loader.utils.ResourceUtils;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基本的 ClassLoader
 *
 * @author starBlues
 * @since 3.0.0
 * @version 3.1.1
 */
public class GenericClassLoader extends URLClassLoader implements ResourceLoaderFactory {

    private final String name;
    private final ClassLoader parent;

    protected final ResourceLoaderFactory resourceLoaderFactory;

    private final ResourceLoaderFactory classLoaderTranslator;

    private final Map<String, Class<?>> pluginClassCache = new ConcurrentHashMap<>();

    public GenericClassLoader(String name, ResourceLoaderFactory resourceLoaderFactory) {
        this(name, null, resourceLoaderFactory);
    }

    public GenericClassLoader(String name, ClassLoader parent, ResourceLoaderFactory resourceLoaderFactory) {
        super(new URL[]{}, null);
        this.name = Assert.isNotEmpty(name, "name 不能为空");
        this.resourceLoaderFactory = Assert.isNotNull(resourceLoaderFactory, "resourceLoaderFactory 不能为空");
        this.parent = parent;
        this.classLoaderTranslator = new ClassLoaderTranslator(this);
    }

    public String getName() {
        return name;
    }

    public ClassLoader getParentClassLoader() {
        return parent;
    }

    @Override
    public void addResource(String path) throws Exception {
        resourceLoaderFactory.addResource(path);
    }

    @Override
    public void addResource(File file) throws Exception {
        resourceLoaderFactory.addResource(file);
    }

    @Override
    public void addResource(Path path) throws Exception {
        resourceLoaderFactory.addResource(path);
    }

    @Override
    public void addResource(URL url) throws Exception {
        resourceLoaderFactory.addResource(url);
    }

    @Override
    public void addResource(Resource resource) throws Exception {
        resourceLoaderFactory.addResource(resource);
    }

    @Override
    public void addResource(ResourceLoader resourceLoader) throws Exception {
        resourceLoaderFactory.addResource(resourceLoader);
    }

    @Override
    public Resource findFirstResource(String name) {
        return classLoaderTranslator.findFirstResource(name);
    }

    @Override
    public Enumeration<Resource> findAllResource(String name) {
        return classLoaderTranslator.findAllResource(name);
    }

    @Override
    public InputStream getInputStream(String name) {
        return classLoaderTranslator.getInputStream(name);
    }

    @Override
    public List<URL> getUrls() {
        return classLoaderTranslator.getUrls();
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(className)) {
            return findClass(className);
        }
    }

    @Override
    public URL[] getURLs() {
        List<URL> urlList = resourceLoaderFactory.getUrls();
        URL[] urls = new URL[urlList.size()];
        for (int i = 0; i < urlList.size(); i++) {
            urls[i] = urlList.get(i);
        }
        return urls;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        name = formatResourceName(name);
        InputStream inputStream = findInputStreamFromParent(name);
        if (inputStream != null) {
            return inputStream;
        }
        return findInputStreamFromLocal(name);
    }

    @Override
    public URL getResource(String name) {
        name = formatResourceName(name);
        URL url = findResourceFromParent(name);
        if (url != null) {
            return url;
        }
        return findResourceFromLocal(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        name = formatResourceName(name);
        Enumeration<URL> parentResources = findResourcesFromParent(name);
        Enumeration<URL> localResources = findResourcesFromLocal(name);
        return new Enumeration<URL>() {

            private int index = 0;

            @Override
            public boolean hasMoreElements() {
                if (parentResources != null && parentResources.hasMoreElements()) {
                    return true;
                }
                index = 1;
                return localResources.hasMoreElements();
            }

            @Override
            public URL nextElement() {
                if (index == 0) {
                    return parentResources.nextElement();
                } else {
                    return localResources.nextElement();
                }
            }
        };
    }

    @Override
    public void close() throws IOException {
        super.close();
        IOUtils.closeQuietly(resourceLoaderFactory);
    }

    @Override
    public void release() {
        ResourceUtils.release(resourceLoaderFactory);
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        Class<?> loadedClass = findClassFromParent(className);
        if (loadedClass != null) {
            return loadedClass;
        }
        loadedClass = findLoadedClass(className);
        if (loadedClass != null) {
            return loadedClass;
        }
        try {
            loadedClass = findClassFromLocal(className);
        } catch (IOException e) {
            throw new ClassNotFoundException("ClassLoader[" + name + "]:" + className);
        }
        if (loadedClass != null) {
            return loadedClass;
        }
        throw new ClassNotFoundException("ClassLoader[" + name + "]:" + className);
    }

    protected Class<?> findClassFromParent(String className) throws ClassNotFoundException {
        try {
            if (parent != null) {
                return parent.loadClass(className);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    protected Class<?> findClassFromLocal(String name) throws IOException {
        Class<?> aClass;
        String formatClassName = formatClassName(name);

        aClass = pluginClassCache.get(formatClassName);
        if (aClass != null) {
            return aClass;
        }

        Resource resource = resourceLoaderFactory.findFirstResource(formatClassName);
        byte[] bytes = null;
        if (resource != null) {
            bytes = resource.getBytes();
        }
        if (bytes == null || bytes.length == 0) {
            bytes = getClassByte(formatClassName);
        }
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        aClass = super.defineClass(name, bytes, 0, bytes.length);
        if (aClass == null) {
            return null;
        }
        if (aClass.getPackage() == null) {
            int lastDotIndex = name.lastIndexOf('.');
            String packageName = (lastDotIndex >= 0) ? name.substring(0, lastDotIndex) : "";
            super.definePackage(packageName, null, null, null,
                    null, null, null, null);
        }
        pluginClassCache.put(name, aClass);
        return aClass;
    }

    protected URL findResourceFromParent(String name) {
        if (parent != null) {
            return parent.getResource(name);
        }
        return null;
    }

    protected URL findResourceFromLocal(String name) {
        Resource resource = resourceLoaderFactory.findFirstResource(name);
        if (resource == null) {
            return null;
        }
        return resource.getUrl();
    }


    protected InputStream findInputStreamFromParent(String name) {
        if (parent != null) {
            return parent.getResourceAsStream(name);
        }
        return null;
    }

    protected InputStream findInputStreamFromLocal(String name) {
        return resourceLoaderFactory.getInputStream(name);
    }

    protected Enumeration<URL> findResourcesFromParent(String name) throws IOException {
        if (parent != null) {
            return parent.getResources(name);
        }
        return null;
    }

    protected Enumeration<URL> findResourcesFromLocal(String name) throws IOException {
        Enumeration<Resource> enumeration = resourceLoaderFactory.findAllResource(name);
        return new Enumeration<URL>() {
            @Override
            public boolean hasMoreElements() {
                return enumeration.hasMoreElements();
            }

            @Override
            public URL nextElement() {
                return enumeration.nextElement().getUrl();
            }
        };
    }

    private byte[] getClassByte(String formatClassName) throws IOException {
        try (InputStream inputStream = resourceLoaderFactory.getInputStream(formatClassName)) {
            if (inputStream == null) {
                return null;
            }
            try {
                return IOUtils.read(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    private String formatResourceName(String name) {
        return ResourceUtils.formatStandardName(name);
    }

    private String formatClassName(String className) {
        className = className.replace('/', '~');
        className = className.replace('.', '/') + ".class";
        className = className.replace('~', '/');
        return className;
    }

}
