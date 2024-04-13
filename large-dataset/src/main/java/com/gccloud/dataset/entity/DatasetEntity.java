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

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gccloud.common.entity.SuperEntity;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.entity.config.BaseDataSetConfig;
import com.gccloud.dataset.typehandler.BaseDataSetConfigTypeHandler;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author zhang.zeJun
 * @date 2022-11-14-9:57
 */
@Data
@TableName(value = "ds_dataset", autoResultMap = true)
@ApiModel("主数据集")
public class DatasetEntity extends SuperEntity {

    @ApiModelProperty(value = "数据集名称")
    private String name;

    @ApiModelProperty(value = "分类ID")
    private String typeId;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "数据集类型")
    private String datasetType;

    @ApiModelProperty(value = "是否可编辑，0 不可编辑 1 可编辑")
    private Integer editable;

    @ApiModelProperty(value = "数据源id")
    private String sourceId;

    @ApiModelProperty(value = "唯一编码")
    private String code;

    /**
     * 参考 {@link DatasetConstant.DatasetCache}
     */
    @ApiModelProperty(value = "是否对执行结果缓存 0 不缓存 1 缓存")
    private Integer cache;

    @ApiModelProperty(value = "具体数据集配置")
    @TableField(typeHandler = BaseDataSetConfigTypeHandler.class)
    private BaseDataSetConfig config;


}