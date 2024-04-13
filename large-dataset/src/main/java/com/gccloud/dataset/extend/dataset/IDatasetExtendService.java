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

package com.gccloud.dataset.extend.dataset;

import com.gccloud.dataset.entity.DatasetEntity;

import java.util.List;
import java.util.Map;

/**
 * 数据集扩展接口，可通过实现该接口来自定义数据集的一些操作
 *
 * @author hongyang
 * @version 1.0
 * @date 2023/7/19 17:26
 */
public interface IDatasetExtendService {

    /**
     * 数据集删除前校验
     * 请返回校验结果，为空（""或null）表示校验通过，否则为校验失败原因
     * 请不要抛出异常，否则可能会导致校验不完整
     * 返回的校验结果会在前端展示给用户
     *
     * @param id 数据集id
     * @return 校验结果，为空（""或null）表示校验通过，否则为校验失败原因
     */
    String deleteCheck(String id);


    /**
     * 实现类所属的业务类型
     * 用于在删除数据集时，区分删除前校验结果归属于哪个业务类型
     *
     * @return
     */
    default String getServiceType() {
        return null;
    }


    /**
     * 数据处理，可通过实现该方法来处理数据集的返回数据
     *
     * @param data          数据集返回数据
     * @param datasetEntity 数据集实体
     * @return 处理后的数据
     */
    default List<Map<String, Object>> handleData(List<Map<String, Object>> data, DatasetEntity datasetEntity) {
        return data;
    }

}