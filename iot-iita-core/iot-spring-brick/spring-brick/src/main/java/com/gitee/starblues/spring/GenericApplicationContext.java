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

package com.gitee.starblues.spring;

import com.gitee.starblues.utils.Assert;

/**
 * 基本的ApplicationContext
 * @author starBlues
 * @version 3.0.0
 */
public class GenericApplicationContext implements ApplicationContext{

    public final AutoCloseable autoCloseable;

    protected SpringBeanFactory springBeanFactory;
    protected Object sourcesBeanFactory;

    public GenericApplicationContext() {
        this(null);
    }

    public GenericApplicationContext(AutoCloseable autoCloseable) {
        this.autoCloseable = autoCloseable;
    }

    public void setSpringBeanFactory(SpringBeanFactory springBeanFactory){
        this.springBeanFactory = Assert.isNotNull(springBeanFactory, "参数 springBeanFactory 不能为空");
    }

    public void setSourcesBeanFactory(Object sourcesBeanFactory) {
        this.sourcesBeanFactory = sourcesBeanFactory;
    }

    @Override
    public SpringBeanFactory getSpringBeanFactory() {
        return springBeanFactory;
    }

    @Override
    public Object getSourceBeanFactory() {
        return sourcesBeanFactory;
    }


    @Override
    public void close() throws Exception {
        if(autoCloseable != null){
            autoCloseable.close();
        }
    }
}
