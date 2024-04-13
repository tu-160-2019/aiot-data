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

package com.gccloud.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 查询DTO，其他查询可以继承该类实现查询条件扩展
 *
 * @Author maoshufeng
 * @Date 2020-06-13 15:08
 * @Version 1.0
 */
@Data
@ApiModel
public class SearchDTO {

    @ApiModelProperty(notes = "当前显示页数")
    private Integer current;

    @ApiModelProperty(notes = "每页展示数据条数")
    private Integer size;

    @ApiModelProperty(notes = "查询条件")
    private String searchKey;

}