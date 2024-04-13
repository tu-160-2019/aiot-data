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

package com.gccloud.common.service.impl;

import com.gccloud.common.service.ITreeService;
import com.gccloud.common.vo.TreeVo;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liuchengbiao
 * @date 2020-07-22 14:34
 */
@Service
public class CommonTreeServiceImpl implements ITreeService {

    private static final String SUPER_PARENT_ID = "0";

    @Override
    public void transToTree(List<? extends TreeVo> voList) {
        Map<String, TreeVo> voMap = new HashMap<>();
        voList.forEach(vo -> voMap.put(vo.getId(), vo));
        // 转为树
        for (TreeVo vo : voList) {
            if (SUPER_PARENT_ID.equals(vo.getParentId())) {
                continue;
            }
            TreeVo parentVo = voMap.get(vo.getParentId());
            if (parentVo == null) {
                continue;
            }
            vo.setParentName(parentVo.getName());
            List<TreeVo> children = parentVo.getChildren();
            if (children == null) {
                children = Lists.newArrayList();
                parentVo.setChildren(children);
            }
            children.add(vo);
        }
    }
}