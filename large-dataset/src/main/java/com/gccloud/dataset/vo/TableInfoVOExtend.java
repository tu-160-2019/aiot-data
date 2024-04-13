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

import lombok.Data;

/**
 * 库表信息扩展类，用于接收sqlserver库表信息
 *
 * @author hongyang
 * @version 1.0
 * @date 2023/8/29 16:42
 */
@Data
public class TableInfoVOExtend extends TableInfoVO {

    /**
     * 表所属数据库名
     */
    private String tableCatalog;

    /**
     * 表所属模式名
     */
    private String tableSchema;

}