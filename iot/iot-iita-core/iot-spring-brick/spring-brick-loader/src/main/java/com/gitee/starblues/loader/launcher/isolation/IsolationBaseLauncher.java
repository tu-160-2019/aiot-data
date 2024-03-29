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

package com.gitee.starblues.loader.launcher.isolation;

import com.gitee.starblues.loader.classloader.GenericClassLoader;
import com.gitee.starblues.loader.classloader.resource.loader.ResourceLoaderFactory;
import com.gitee.starblues.loader.launcher.AbstractMainLauncher;
import com.gitee.starblues.loader.launcher.runner.MethodRunner;
import com.gitee.starblues.loader.utils.ObjectUtils;
import com.gitee.starblues.loader.utils.ResourceUtils;


import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 主程序启动者
 *
 * @author starBlues
 * @since 3.0.0
 * @version 3.0.0
 */
public class IsolationBaseLauncher extends AbstractMainLauncher {

    private final MethodRunner methodRunner;

    public IsolationBaseLauncher(MethodRunner methodRunner) {
        this.methodRunner = methodRunner;
    }

    @Override
    protected boolean resolveThreadClassLoader() {
        return false;
    }

    @Override
    protected ClassLoader createClassLoader(String... args) throws Exception {
        return new GenericClassLoader(MAIN_CLASS_LOADER_NAME, getParentClassLoader(),
                getResourceLoaderFactory(args));
    }

    @Override
    protected ClassLoader launch(ClassLoader classLoader, String... args) throws Exception {
        methodRunner.run(classLoader);
        ResourceUtils.release(classLoader);
        return classLoader;
    }

    protected ResourceLoaderFactory getResourceLoaderFactory(String... args){
        return ResourceLoaderFactoryGetter.get(MAIN_CLASS_LOADER_NAME, args);
    }

    protected ClassLoader getParentClassLoader(){
        return IsolationBaseLauncher.class.getClassLoader();
    }

    protected Set<URL> getBaseResource() {
        Set<URL> urls = new HashSet<>();
        String classPath = ManagementFactory.getRuntimeMXBean().getClassPath();
        if(!ObjectUtils.isEmpty(classPath)){
            String[] classPathStr = classPath.split(";");
            for (String path : classPathStr) {
                try {
                    urls.add(new URL(path));
                } catch (MalformedURLException e) {
                    // 忽略
                }
            }
        }
        ClassLoader sourceClassLoader = Thread.currentThread().getContextClassLoader();
        if(sourceClassLoader instanceof URLClassLoader){
            URLClassLoader urlClassLoader = (URLClassLoader) sourceClassLoader;
            final URL[] urLs = urlClassLoader.getURLs();
            urls.addAll(Arrays.asList(urLs));
        }
        return urls;
    }

}
