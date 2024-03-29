package com.gitee.starblues.bootstrap.launcher;

import com.gitee.starblues.bootstrap.DefaultSpringPluginHook;
import com.gitee.starblues.bootstrap.PluginOneselfSpringApplication;
import com.gitee.starblues.bootstrap.PluginSpringApplication;
import com.gitee.starblues.bootstrap.SpringPluginBootstrap;
import com.gitee.starblues.bootstrap.processor.DefaultProcessorContext;
import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import com.gitee.starblues.bootstrap.processor.SpringPluginProcessor;
import com.gitee.starblues.core.launcher.plugin.PluginInteractive;
import com.gitee.starblues.spring.SpringPluginHook;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;

/**
 * 插件自主启动配置
 *
 * @author starBlues
 * @version 3.1.0
 * @since 3.0.4
 */
@AllArgsConstructor
public class OneselfBootstrapLauncher implements BootstrapLauncher{

    private final SpringPluginBootstrap bootstrap;
    private final SpringPluginProcessor pluginProcessor;
    private final PluginInteractive pluginInteractive;


    @Override
    public SpringPluginHook launch(Class<?>[] primarySources, String[] args) {
        ProcessorContext.RunMode runMode = bootstrap.getRunMode();

        ProcessorContext processorContext = new DefaultProcessorContext(
                runMode, bootstrap, pluginInteractive, bootstrap.getClass()
        );
        SpringApplication springApplication = new PluginOneselfSpringApplication(
                pluginProcessor,
                processorContext,
                primarySources);
        springApplication.run(args);
        return new DefaultSpringPluginHook(pluginProcessor, processorContext);
    }



}
