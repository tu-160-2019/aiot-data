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

package com.gitee.starblues.bootstrap.launcher;

import com.gitee.starblues.bootstrap.PluginDisableAutoConfiguration;
import com.gitee.starblues.bootstrap.SpringPluginBootstrap;
import com.gitee.starblues.bootstrap.processor.ComposeSpringPluginProcessor;
import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import com.gitee.starblues.bootstrap.processor.SpringPluginProcessor;
import com.gitee.starblues.core.launcher.plugin.PluginInteractive;
import com.gitee.starblues.loader.launcher.DevelopmentModeSetting;

import java.util.List;

/**
 * 默认的 BootstrapLauncher 创造工厂
 *
 * @author starBlues
 * @since 3.0.4
 * @version 3.1.0
 */
public class DefaultBootstrapLauncherFactory implements BootstrapLauncherFactory{
    @Override
    public BootstrapLauncher create(SpringPluginBootstrap bootstrap) {
        PluginDisableAutoConfiguration.setLaunchPlugin();
        ProcessorContext.RunMode runMode = bootstrap.getRunMode();
        List<SpringPluginProcessor> customPluginProcessors = bootstrap.getCustomPluginProcessors();
        PluginInteractive pluginInteractive = bootstrap.getPluginInteractive();

        SpringPluginProcessor pluginProcessor = new ComposeSpringPluginProcessor(runMode, customPluginProcessors);
        BootstrapLauncher bootstrapLauncher = null;
        if(DevelopmentModeSetting.isolation()){
            bootstrapLauncher = new IsolationBootstrapLauncher(bootstrap, pluginProcessor, pluginInteractive);
        } else if(DevelopmentModeSetting.coexist()){
            bootstrapLauncher = new CoexistBootstrapLauncher(bootstrap, pluginProcessor, pluginInteractive);
        } else {
            bootstrapLauncher = new OneselfBootstrapLauncher(bootstrap, pluginProcessor, pluginInteractive);
        }
        return bootstrapLauncher;
    }
}
