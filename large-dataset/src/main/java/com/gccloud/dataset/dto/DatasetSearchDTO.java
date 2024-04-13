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

import com.gccloud.common.dto.SearchDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 10:58
 */
@Data
public class DatasetSearchDTO extends SearchDTO {

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "数据集编码")
    private String code;

    @ApiModelProperty(value = "种类id")
    private String typeId;

    @ApiModelProperty(value = "数据集类型")
    private List<String> datasetType;

    @ApiModelProperty(value = "所属数据源id")
    private String sourceId;

    @ApiModelProperty(value = "关联标签id列表")
    private List<String> labelIds;

    @ApiModelProperty(value = "数据集id列表")
    private List<String> datasetIds;

    @ApiModelProperty(value = "定位id，用于获取该数据集id位于的分页位置")
    private String positionId;


}