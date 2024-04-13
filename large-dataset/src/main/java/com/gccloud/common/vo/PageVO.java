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

package com.gccloud.common.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装
 */
@ApiModel(description = "分页")
@Data
public class PageVO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(notes = "总条数")
    private long totalCount;

    @ApiModelProperty(notes = "每页条数")
    private long size;

    @ApiModelProperty(notes = "总页数")
    private long totalPage;

    @ApiModelProperty(notes = "当前页数")
    private long current;

    @ApiModelProperty(notes = "列表数据")
    private List<T> list;

    /**
     * 分页
     */
    public PageVO(IPage<T> page) {
        this.list = page.getRecords();
        this.totalCount = page.getTotal();
        this.size = page.getSize();
        this.current = page.getCurrent();
        this.totalPage = page.getPages();
    }

    public PageVO() {
    }

}