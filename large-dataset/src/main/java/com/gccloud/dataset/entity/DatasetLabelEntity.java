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

package com.gccloud.dataset.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gccloud.common.utils.EmptyAsNullDeserializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 数据集与标签关联表
 *
 * @author hongyang
 * @version 1.0
 * @date 2023/7/5 10:13
 */
@Data
@TableName("ds_dataset_label")
@ApiModel("数据集与标签关联表")
public class DatasetLabelEntity {

    @TableId
    @JsonDeserialize(using = EmptyAsNullDeserializer.class)
    @ApiModelProperty(notes = "主键")
    private String id;

    @ApiModelProperty(value = "关联数据集ID")
    private String datasetId;

    @ApiModelProperty(value = "关联标签ID")
    private String labelId;


}