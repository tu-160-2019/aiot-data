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

import com.gitee.starblues.core.PluginInsideInfo;
import com.gitee.starblues.core.classloader.*;
import com.gitee.starblues.core.launcher.plugin.involved.PluginLaunchInvolved;
import com.gitee.starblues.loader.classloader.GenericClassLoader;
import com.gitee.starblues.loader.classloader.resource.loader.DefaultResourceLoaderFactory;
import com.gitee.starblues.loader.classloader.resource.loader.ResourceLoaderFactory;
import com.gitee.starblues.loader.launcher.LauncherContext;
import com.gitee.starblues.loader.utils.ResourceUtils;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.spring.SpringPluginHook;
import com.gitee.starblues.utils.MsgUtils;
import com.gitee.starblues.utils.SpringBeanCustomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 插件隔离式启动引导
 *
 * @author starBlues
 * @since 3.0.0
 * @version 3.1.0
 */
public class PluginIsolationLauncher extends AbstractPluginLauncher {

    private static final Map<String, PluginClassLoader> CLASS_LOADER_CACHE = new WeakHashMap<>();

    protected final PluginInsideInfo pluginInsideInfo;
    protected final MainResourceMatcher mainResourceMatcher;

    public PluginIsolationLauncher(PluginInteractive pluginInteractive,
                                   PluginLaunchInvolved pluginLaunchInvolved) {
        super(pluginInteractive, pluginLaunchInvolved);
        this.pluginInsideInfo = pluginInteractive.getPluginInsideInfo();
        this.mainResourceMatcher = getMainResourceMatcher(pluginInteractive);
    }

    protected MainResourceMatcher getMainResourceMatcher(PluginInteractive pluginInteractive){
        MainApplicationContext mainApplicationContext = pluginInteractive.getMainApplicationContext();
        // 获取主程序定义的资源匹配
        List<MainResourceMatcher> mainResourceMatchers =
                SpringBeanCustomUtils.getBeans(mainApplicationContext, MainResourceMatcher.class);

        List<MainResourceMatcher> resourceMatchers = new ArrayList<>(mainResourceMatchers);
        // 新增插件定义的资源匹配
        resourceMatchers.add(new DefaultMainResourceMatcher(
                new PluginMainResourcePatternDefiner(pluginInteractive)
        ));
        return new ComposeMainResourceMatcher(resourceMatchers);
    }

    @Override
    protected ClassLoader createPluginClassLoader(String... args) throws Exception {
        PluginClassLoader pluginClassLoader = getPluginClassLoader();
        pluginClassLoader.addResource(pluginInsideInfo.getPluginDescriptor());
        return pluginClassLoader;
    }

    @Override
    protected SpringPluginHook launch(ClassLoader classLoader, String... args) throws Exception {
        SpringPluginHook springPluginHook = super.launch(classLoader, args);
        ResourceUtils.release(classLoader);
        return springPluginHook;
    }

    protected synchronized PluginClassLoader getPluginClassLoader() throws Exception {
        String pluginId = pluginInsideInfo.getPluginId();
        String key = MsgUtils.getPluginUnique(pluginInsideInfo.getPluginDescriptor());
        PluginClassLoader classLoader = CLASS_LOADER_CACHE.get(key);
        if(classLoader != null){
            return classLoader;
        }
        PluginClassLoader pluginClassLoader = new PluginClassLoader(
                pluginId, getParentClassLoader(),  getParentClassLoader(), getResourceLoaderFactory(),
                mainResourceMatcher
        );
        CLASS_LOADER_CACHE.put(key, pluginClassLoader);
        return pluginClassLoader;
    }

    protected ResourceLoaderFactory getResourceLoaderFactory(){
        return new DefaultResourceLoaderFactory(pluginInsideInfo.getPluginId());
    }

    protected GenericClassLoader getParentClassLoader() throws Exception {
        ClassLoader contextClassLoader = LauncherContext.getMainClassLoader();
        if(contextClassLoader instanceof GenericClassLoader){
            return (GenericClassLoader) contextClassLoader;
        } else {
            throw new Exception("非法父类加载器: " + contextClassLoader.getClass().getName());
        }
    }

}
