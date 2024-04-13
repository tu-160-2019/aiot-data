/*
 * Copyright 2023 http://gcpaas.gccloud.com
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

package com.gccloud.dataset.service.factory;

import com.gccloud.dataset.service.IBaseDatasourceService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Locale;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 11:42
 */
@Service
public class DatasourceServiceFactory {

    @Resource
    private ApplicationContext applicationContext;

    public IBaseDatasourceService build(String type) {
        return applicationContext.getBean(type.toLowerCase(Locale.ROOT), IBaseDatasourceService.class);
    }

}