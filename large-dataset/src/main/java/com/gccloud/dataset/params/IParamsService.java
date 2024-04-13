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

package com.gccloud.dataset.params;

import com.gccloud.dataset.dto.DatasetParamDTO;

import java.util.List;

/**
 * 数据集参数处理，可通过实现该接口来自定义参数处理逻辑
 *
 * @author hongyang
 * @version 1.0
 * @date 2023/5/22 13:58
 */
public interface IParamsService {


    /**
     * 处理数据集参数
     *
     * @param params 数据集参数
     * @return 处理后的数据集参数
     */
    List<DatasetParamDTO> handleParams(List<DatasetParamDTO> params);


    /**
     * 自定义处理脚本
     *
     * @param datasetType 数据集类型
     * @param script      脚本
     * @return 处理后的脚本
     */
    String handleScript(String datasetType, String script);

}