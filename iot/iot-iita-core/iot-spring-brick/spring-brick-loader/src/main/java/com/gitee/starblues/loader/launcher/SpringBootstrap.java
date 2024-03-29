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
 * 主程序实现该接口引导启动SpringBoot
 *
 * @author starBlues
 * @since 3.0.0
 * @version 3.0.0
 */
public interface SpringBootstrap {

    /**
     * 启动
     * @param args 启动参数
     * @throws Exception 启动异常
     */
    void run(String[] args) throws Exception;


    /**
     * 设置开发模式
     *
     * @return DevelopmentMode
     */
    default String developmentMode(){
        return DevelopmentMode.ISOLATION;
    }

}
