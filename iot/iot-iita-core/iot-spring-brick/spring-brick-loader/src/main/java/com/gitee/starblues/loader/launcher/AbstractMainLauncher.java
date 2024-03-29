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

package com.gitee.starblues.loader.launcher;

import com.gitee.starblues.loader.DevelopmentMode;

/**
 * 抽象的启动引导者
 *
 * @author starBlues
 * @since 3.0.0
 * @version 3.0.2
 */
public abstract class AbstractMainLauncher extends AbstractLauncher<ClassLoader> {

    public static final String MAIN_CLASS_LOADER_NAME = "MainProgramLauncherClassLoader";

    /**
     * SpringPluginBootstrap 包名称
     * @since 3.0.4
     */
    private final static String SPRING_PLUGIN_BOOTSTRAP_PACKAGE_NAME = "com.gitee.starblues.bootstrap.SpringPluginBootstrap";

    /**
     * SpringPluginBootstrap 依赖坐标
     * @since 3.0.4
     */
    private final static String SPRING_PLUGIN_BOOTSTRAP_COORDINATE = "com.gitee.starblues:spring-brick-bootstrap";


    @Override
    public ClassLoader run(String... args) throws Exception {
        ClassLoader classLoader = createClassLoader(args);
        if(!resolveThreadClassLoader()){
            return toLaunch(classLoader, args);
        }
        Thread thread = Thread.currentThread();
        ClassLoader oldClassLoader  = thread.getContextClassLoader();
        try {
            thread.setContextClassLoader(classLoader);
            return toLaunch(classLoader, args);
        } finally {
            thread.setContextClassLoader(oldClassLoader);
        }
    }

    protected ClassLoader toLaunch(ClassLoader classLoader, String... args) throws Exception{
        LauncherContext.setMainClassLoader(classLoader);
        checkSpringPluginBootstrap(classLoader);
        return launch(classLoader, args);
    }

    protected boolean resolveThreadClassLoader(){
        return true;
    }

    /**
     * 检查 {@link this#SPRING_PLUGIN_BOOTSTRAP_COORDINATE} 依赖是否配置合适
     * @param classLoader 当前主程序classloader
     * @throws RuntimeException 检查一次
     */
    private void checkSpringPluginBootstrap(ClassLoader classLoader) throws RuntimeException{
        try {
            classLoader.loadClass(SPRING_PLUGIN_BOOTSTRAP_PACKAGE_NAME);
            if(DevelopmentModeSetting.isolation()){
                // 主程序加载到了
                throw new RuntimeException("[" + DevelopmentMode.ISOLATION + "]模式下" +
                        "不能将[" + SPRING_PLUGIN_BOOTSTRAP_COORDINATE + "]依赖定义到主程序中, 只能依赖到插件中!");
            }
        } catch (ClassNotFoundException e) {
            if(!DevelopmentModeSetting.isolation()){
                throw new RuntimeException("[" + DevelopmentMode.COEXIST + "]模式" +
                        "需要将[" + SPRING_PLUGIN_BOOTSTRAP_COORDINATE + "]依赖定义到主程序中!");
            }
        }
    }

}
