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

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 10:43
 */
@Data
public class CustomDataSetConfig extends BaseDataSetConfig {

    @ApiModelProperty(value = "数据源id")
    private String sourceId;

    @ApiModelProperty(value = "自定义Sql")
    private String sqlProcess;

    @ApiModelProperty(value = "字段描述")
    private Map<String, Object> fieldDesc;

    @ApiModelProperty(value = "语法类型 normal:普通 mybatis:mybatis")
    private String syntaxType;

    /**
     * 暂未使用
     */
    @ApiModelProperty(value = "数据集编码")
    private String code;

    /**
     * 暂未使用
     */
    @ApiModelProperty(value = "结构缓存")
    private String cacheField;

    /**
     * 暂未使用
     */
    @ApiModelProperty(value = "结果转换脚本")
    private String script;


}