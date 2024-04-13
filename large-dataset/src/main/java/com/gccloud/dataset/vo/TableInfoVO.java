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
 * 数据库表/视图信息
 *
 * @author hongyang
 * @version 1.0
 * @date 2023/6/6 14:42
 */
@Data
public class TableInfoVO {

    @ApiModelProperty(value = "表名")
    private String name;

    @ApiModelProperty(value = "状态，用于表示该表是否已经创建过原始数据集 0：未创建 1：已创建")
    private Integer status;

}