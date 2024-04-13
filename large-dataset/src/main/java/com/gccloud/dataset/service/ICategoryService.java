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

package com.gccloud.dataset.service;

import com.gccloud.common.service.ISuperService;
import com.gccloud.dataset.dto.CategorySearchDTO;
import com.gccloud.dataset.entity.CategoryEntity;
import com.gccloud.dataset.vo.CategoryVO;

import java.util.List;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/2 13:43
 */
public interface ICategoryService extends ISuperService<CategoryEntity> {


    /**
     * 获取分类树
     *
     * @param searchDTO
     * @return
     */
    List<CategoryVO> getTree(CategorySearchDTO searchDTO);


    /**
     * 获取节点的所有子节点id，包括子节点的子节点的...
     *
     * @param id
     * @return
     */
    List<String> getAllChildrenId(String id);

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    String add(CategoryEntity entity);

    /**
     * 修改
     *
     * @param entity
     */
    void update(CategoryEntity entity);

    /**
     * 删除
     *
     * @param id
     */
    void delete(String id);


    /**
     * 校验名称重复
     *
     * @param entity
     * @return
     */
    boolean checkNameRepeat(CategoryEntity entity);


}