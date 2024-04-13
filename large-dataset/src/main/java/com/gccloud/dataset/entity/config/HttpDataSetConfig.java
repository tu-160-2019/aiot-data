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

import com.gccloud.dataset.constant.DatasetConstant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * http接口数据集配置
 *
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 10:43
 */
@Data
public class HttpDataSetConfig extends BaseDataSetConfig {

    @ApiModelProperty("数据集类型")
    private String datasetType = DatasetConstant.DataSetType.HTTP;

    @ApiModelProperty(value = "请求地址")
    private String url;

    @ApiModelProperty(value = "请求类型 GET POST")
    private String method;

    @ApiModelProperty(value = "请求方式 前端 后端")
    private String requestType;

    @ApiModelProperty(value = "请求头")
    private List<Map<String, Object>> headers;

    @ApiModelProperty(value = "请求参数")
    private List<Map<String, Object>> params;

    @ApiModelProperty(value = "请求体")
    private String body;

    @ApiModelProperty(value = "请求脚本")
    private String requestScript;

    @ApiModelProperty(value = "响应脚本")
    private String responseScript;

}