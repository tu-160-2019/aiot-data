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

package com.gccloud.dataset.permission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 数据集权限处理
 *
 * @author hongyang
 * @version 1.0
 * @date 2023/7/3 9:41
 */
@Component
public class DatasetPermissionClient {


    @Autowired(required = false)
    private IDatasetPermissionService permissionService;

    /**
     * 是否有实现类
     *
     * @return
     */
    public boolean hasPermissionService() {
        return permissionService != null;
    }

    /**
     * 根据权限过滤
     *
     * @param allId 全部的数据集id
     * @return 当前用户有权限的数据集id
     */
    public List<String> filterByPermission(List<String> allId, List<String> datasetTypeList) {
        if (permissionService != null) {
            return permissionService.filterByPermission(allId, datasetTypeList);
        }
        return allId;
    }

    /**
     * 数据集新增后的权限处理
     *
     * @param id 新增的数据集id
     */
    public void addPermission(String id) {
        if (permissionService != null) {
            permissionService.addPermission(id);
        }
    }

    /**
     * 数据集删除后的权限处理
     *
     * @param id 删除的数据集id
     */
    public void deletePermission(String id) {
        if (permissionService != null) {
            permissionService.deletePermission(id);
        }
    }
}