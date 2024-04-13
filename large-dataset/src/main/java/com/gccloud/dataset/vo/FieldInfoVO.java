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

package com.gccloud.dataset.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 数据库表/视图字段信息
 *
 * @author hongyang
 * @version 1.0
 * @date 2023/6/6 14:42
 */
@Data
public class FieldInfoVO {

    @ApiModelProperty(value = "列名")
    private String columnName;

    @ApiModelProperty(value = "列名注释")
    private String columnComment;

    @ApiModelProperty(value = "列类型")
    private String columnType;


}