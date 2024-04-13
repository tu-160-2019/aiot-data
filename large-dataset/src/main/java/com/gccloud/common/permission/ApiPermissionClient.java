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

package com.gccloud.common.permission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/5/15 10:43
 */
@Component
public class ApiPermissionClient {

    @Autowired(required = false)
    private IApiPermissionService apiPermissionService;

    /**
     * 是否有实现类
     *
     * @return
     */
    public boolean hasPermissionService() {
        return apiPermissionService != null;
    }

    public boolean verifyApiPermission(HttpServletRequest request, String... permissions) {
        boolean verify = true;
        if (apiPermissionService != null) {
            verify = apiPermissionService.verifyApiPermission(request, permissions);
        }
        return verify;
    }


}