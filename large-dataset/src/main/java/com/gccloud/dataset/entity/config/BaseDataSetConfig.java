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

package com.gccloud.dataset.entity.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.gccloud.dataset.dto.DatasetParamDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 10:38
 */
@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "className")
public class BaseDataSetConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "数据集字段信息")
    private List<Map<String, Object>> fieldList;

    @ApiModelProperty(value = "参数配置信息")
    private List<DatasetParamDTO> paramsList;

}