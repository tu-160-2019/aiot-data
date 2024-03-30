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

package com.gitee.starblues.integration.listener;

import org.springframework.context.ApplicationContext;

/**
 * 默认的初始化监听者。内置注册
 *
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultInitializerListener implements PluginInitializerListener{


    public DefaultInitializerListener(ApplicationContext applicationContext) {
    }


    @Override
    public void before() {

    }

    @Override
    public void complete() {
        refresh();
    }

    @Override
    public void failure(Throwable throwable) {
        refresh();
    }

    private void refresh(){
    }

}
