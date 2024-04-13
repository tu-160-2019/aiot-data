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

package com.gccloud.dataset.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Excel数据源表头DTO
 *
 * @author hongyang
 * @version 1.0
 * @date 2023/7/3 10:45
 */
@Data
public class ExcelHeaderDTO {

    @ApiModelProperty(value = "表头序号")
    private Integer index;

    @ApiModelProperty(value = "表头名称")
    private String title;

    @ApiModelProperty(value = "字段名称")
    private String columnName;

    @ApiModelProperty(value = "字段类型")
    private String columnType;


}