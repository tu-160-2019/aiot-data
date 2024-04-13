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

package com.gccloud.common.service;

import com.gccloud.common.vo.TreeVo;

import java.util.List;

/**
 * @author liuchengbiao
 * @date 2020-07-22 14:33
 */
public interface ITreeService {
    /**
     * 将平铺的数据转为符合树形的数据结构
     *
     * @param voList
     */
    void transToTree(List<? extends TreeVo> voList);

}