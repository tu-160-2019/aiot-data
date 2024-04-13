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
 * @Description:数据集参数
 * @Author yang.hw
 * @Date 2021/9/15 11:11
 */
@Data
public class DatasetParamDTO {

    @ApiModelProperty(value = "参数名称")
    private String name;

    /**
     * 参考：{@link com.gccloud.dataset.constant.DatasetConstant.SqlParamsType}
     */
    @ApiModelProperty(value = "参数类型")
    private String type;

    @ApiModelProperty(value = "参数值")
    private String value;

    @ApiModelProperty(value = "测试参数值")
    private String testValue;

    @ApiModelProperty(value = "参数状态")
    private Integer status;

    @ApiModelProperty(value = "是否必填")
    private Integer require;

    @ApiModelProperty(value = "备注")
    private String remark;
}