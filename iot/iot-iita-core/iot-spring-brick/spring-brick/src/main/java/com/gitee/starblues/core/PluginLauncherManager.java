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

package com.gitee.starblues.core;

import com.gitee.starblues.core.checker.PluginLauncherChecker;
import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.core.exception.PluginProhibitStopException;
import com.gitee.starblues.core.launcher.plugin.DefaultPluginInteractive;
import com.gitee.starblues.core.launcher.plugin.PluginInteractive;
import com.gitee.starblues.core.launcher.plugin.PluginCoexistLauncher;
import com.gitee.starblues.core.launcher.plugin.PluginIsolationLauncher;
import com.gitee.starblues.core.launcher.plugin.involved.PluginLaunchInvolved;
import com.gitee.starblues.core.launcher.plugin.involved.PluginLaunchInvolvedFactory;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.listener.DefaultPluginListenerFactory;
import com.gitee.starblues.integration.listener.PluginListenerFactory;
import com.gitee.starblues.loader.launcher.AbstractLauncher;
import com.gitee.starblues.loader.launcher.DevelopmentModeSetting;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.spring.MainApplicationContextProxy;
import com.gitee.starblues.spring.SpringPluginHook;
import com.gitee.starblues.spring.invoke.DefaultInvokeSupperCache;
import com.gitee.starblues.spring.invoke.InvokeSupperCache;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.springframework.context.support.GenericApplicationContext;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 可引导启动的插件管理者
 * @author starBlues
 * @since 3.0.0
 * @version 3.1.2
 */
public class PluginLauncherManager extends DefaultPluginManager{

    private final Map<String, RegistryPluginInfo> registryInfo = new ConcurrentHashMap<>();


    private final MainApplicationContext mainApplicationContext;
    private final GenericApplicationContext mainGenericApplicationContext;
    private final IntegrationConfiguration configuration;
    private final InvokeSupperCache invokeSupperCache;
    private final PluginLaunchInvolved pluginLaunchInvolved;

    public PluginLauncherManager(RealizeProvider realizeProvider,
                                 GenericApplicationContext applicationContext,
                                 IntegrationConfiguration configuration) {
        super(realizeProvider, configuration);
        this.mainApplicationContext = new MainApplicationContextProxy(applicationContext, applicationContext);
        this.mainGenericApplicationContext = applicationContext;
        this.configuration = configuration;
        this.invokeSupperCache = new DefaultInvokeSupperCache();
        this.pluginLaunchInvolved = new PluginLaunchInvolvedFactory();
        addCustomPluginChecker();
    }

    private void addCustomPluginChecker(){
        List<PluginLauncherChecker> pluginCheckers = SpringBeanUtils.getBeans(mainGenericApplicationContext,
                PluginLauncherChecker.class);
        for (PluginLauncherChecker pluginChecker : pluginCheckers) {
            super.launcherChecker.add(pluginChecker);
        }
    }

    @Override
    protected PluginListenerFactory createPluginListenerFactory() {
        return new DefaultPluginListenerFactory(mainGenericApplicationContext);
    }

    @Override
    public synchronized List<PluginInfo> loadPlugins() {
        this.pluginLaunchInvolved.initialize(mainGenericApplicationContext, configuration);
        return super.loadPlugins();
    }

    @Override
    protected void start(PluginInsideInfo pluginInsideInfo) throws Exception {
        launcherChecker.checkCanStart(pluginInsideInfo);
        try {
            InsidePluginDescriptor pluginDescriptor = pluginInsideInfo.getPluginDescriptor();
            PluginInteractive pluginInteractive = new DefaultPluginInteractive(pluginInsideInfo,
                    mainApplicationContext, configuration, invokeSupperCache);
            AbstractLauncher<SpringPluginHook> pluginLauncher;
            if(DevelopmentModeSetting.isolation()){
                pluginLauncher = new PluginIsolationLauncher(pluginInteractive, pluginLaunchInvolved);
            } else if(DevelopmentModeSetting.coexist()){
                pluginLauncher = new PluginCoexistLauncher(pluginInteractive, pluginLaunchInvolved);
            } else {
                throw DevelopmentModeSetting.getUnknownModeException();
            }
            SpringPluginHook springPluginHook = pluginLauncher.run();
            RegistryPluginInfo registryPluginInfo = new RegistryPluginInfo(pluginDescriptor, springPluginHook);
            registryInfo.put(pluginDescriptor.getPluginId(), registryPluginInfo);
            pluginInsideInfo.setPluginState(PluginState.STARTED);
            super.startFinish(pluginInsideInfo);
        } catch (Exception e){
            // 启动失败, 进行停止
            pluginInsideInfo.setPluginState(PluginState.STARTED_FAILURE);
            throw e;
        }
    }

    @Override
    protected void stop(PluginInsideInfo pluginInsideInfo, PluginCloseType closeType) throws Exception {
        launcherChecker.checkCanStop(pluginInsideInfo);
        String pluginId = pluginInsideInfo.getPluginId();
        RegistryPluginInfo registryPluginInfo = registryInfo.get(pluginId);
        if(registryPluginInfo == null){
            throw new PluginException("没有发现插件 '" + pluginId +  "' 信息");
        }
        try {
            SpringPluginHook springPluginHook = registryPluginInfo.getSpringPluginHook();
            // 校验是否可停止
            springPluginHook.stopVerify();
            // 关闭插件
            springPluginHook.close(closeType);
            // 移除插件相互调用缓存的信息
            invokeSupperCache.remove(pluginId);
            // 移除插件注册信息
            registryInfo.remove(pluginId);
            super.stop(pluginInsideInfo, closeType);
        } catch (Exception e){
            if(e instanceof PluginProhibitStopException){
                // 禁止停止时, 不设置插件状态
                throw e;
            }
            pluginInsideInfo.setPluginState(PluginState.STOPPED_FAILURE);
            throw e;
        }
    }


    static class RegistryPluginInfo{
        private final PluginDescriptor descriptor;
        private final SpringPluginHook springPluginHook;

        private RegistryPluginInfo(PluginDescriptor descriptor, SpringPluginHook springPluginHook) {
            this.descriptor = descriptor;
            this.springPluginHook = springPluginHook;
        }

        public PluginDescriptor getDescriptor() {
            return descriptor;
        }

        public SpringPluginHook getSpringPluginHook() {
            return springPluginHook;
        }
    }
}
