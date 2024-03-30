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

package com.gitee.starblues.loader.launcher;

import com.gitee.starblues.loader.launcher.coexist.CoexistBaseLauncher;
import com.gitee.starblues.loader.launcher.isolation.IsolationBaseLauncher;
import com.gitee.starblues.loader.launcher.runner.MethodRunner;
import lombok.AllArgsConstructor;

/**
 * 开发环境 Launcher
 *
 * @author starBlues
 * @since 3.0.4
 * @version 3.1.0
 */
@AllArgsConstructor
public class DevLauncher implements Launcher<ClassLoader>{

    private final SpringBootstrap springBootstrap;

    @Override
    public ClassLoader run(String... args) throws Exception {
        MethodRunner methodRunner = new MethodRunner(springBootstrap.getClass().getName(),
                SPRING_BOOTSTRAP_RUN_METHOD, args);
        AbstractMainLauncher launcher;
        if(DevelopmentModeSetting.isolation()){
            launcher = new IsolationBaseLauncher(methodRunner);
        } else if(DevelopmentModeSetting.coexist()) {
            launcher = new CoexistBaseLauncher(methodRunner);
        } else {
            throw DevelopmentModeSetting.getUnknownModeException();
        }
        return launcher.run(args);
    }
}
