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

import java.util.List;

/**
 * 数据集权限处理，可通过实现该接口来自定义权限处理逻辑
 *
 * @author hongyang
 * @version 1.0
 * @date 2023/7/3 9:18
 */
public interface IDatasetPermissionService {


    /**
     * 根据权限过滤
     *
     * @param allId           全部的数据集id
     * @param datasetTypeList 数据集类型列表
     * @return 当前用户有权限的数据集id
     */
    List<String> filterByPermission(List<String> allId, List<String> datasetTypeList);

    /**
     * 数据集新增后的权限处理
     *
     * @param id 新增的数据集id
     */
    void addPermission(String id);

    /**
     * 数据集删除后的权限处理
     *
     * @param id 删除的数据集id
     */
    void deletePermission(String id);


}