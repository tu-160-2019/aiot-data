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

package com.gccloud.dataset.service.impl.label;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gccloud.dataset.dao.DatasetLabelDao;
import com.gccloud.dataset.entity.DatasetLabelEntity;
import com.gccloud.dataset.entity.LabelEntity;
import com.gccloud.dataset.service.IDatasetLabelService;
import com.gccloud.dataset.vo.DatasetLabelVO;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/7/5 14:01
 */
@Service
public class DatasetLabelServiceImpl extends ServiceImpl<DatasetLabelDao, DatasetLabelEntity> implements IDatasetLabelService {

    @Override
    public List<DatasetLabelVO> getDatasetByLabelId(String labelId) {
        return getBaseMapper().getDatasetByLabelId(labelId);
    }

    @Override
    public List<LabelEntity> getLabelByDatasetId(String datasetId) {
        return getBaseMapper().getLabelByDatasetId(datasetId);
    }

    @Override
    public void delete(String datasetId, String labelId) {
        LambdaQueryWrapper<DatasetLabelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DatasetLabelEntity::getDatasetId, datasetId);
        wrapper.eq(DatasetLabelEntity::getLabelId, labelId);
        this.remove(wrapper);
    }

    @Override
    public void deleteByDatasetId(String datasetId) {
        LambdaQueryWrapper<DatasetLabelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DatasetLabelEntity::getDatasetId, datasetId);
        this.remove(wrapper);
    }

    @Override
    public void deleteByLabelId(String labelId) {
        LambdaQueryWrapper<DatasetLabelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DatasetLabelEntity::getLabelId, labelId);
        this.remove(wrapper);
    }

    @Override
    public void addByDatasetId(String datasetId, List<String> labelIds) {
        if (StringUtils.isBlank(datasetId)) {
            return;
        }
        if (labelIds == null || labelIds.isEmpty()) {
            return;
        }
        List<DatasetLabelEntity> list = Lists.newArrayList();
        for (String labelId : labelIds) {
            DatasetLabelEntity entity = new DatasetLabelEntity();
            entity.setDatasetId(datasetId);
            entity.setLabelId(labelId);
            list.add(entity);
        }
        this.saveBatch(list);
    }

    @Override
    public void addByLabelId(String labelId, List<String> datasetIds) {
        if (StringUtils.isBlank(labelId)) {
            return;
        }
        if (datasetIds == null || datasetIds.isEmpty()) {
            return;
        }
        List<DatasetLabelEntity> list = Lists.newArrayList();
        for (String datasetId : datasetIds) {
            DatasetLabelEntity entity = new DatasetLabelEntity();
            entity.setDatasetId(datasetId);
            entity.setLabelId(labelId);
            list.add(entity);
        }
        this.saveBatch(list);
    }

    @Override
    public List<String> getDatasetIdsByLabelIds(List<String> labelIds) {
        LambdaQueryWrapper<DatasetLabelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(DatasetLabelEntity::getDatasetId);
        wrapper.in(DatasetLabelEntity::getLabelId, labelIds);
        List<DatasetLabelEntity> list = this.list(wrapper);
        // 取id，且去重
        List<String> datasetIds = list.stream().map(DatasetLabelEntity::getDatasetId).distinct().collect(Lists::newArrayList, List::add, List::addAll);
        return datasetIds;
    }

    @Override
    public List<String> getDatasetIdsByLabelIds(List<String> labelIds, boolean allMatch) {
        if (!allMatch) {
            return getDatasetIdsByLabelIds(labelIds);
        }
        // 全匹配，要求数据集和所有labelIds中的标签都有关联
        LambdaQueryWrapper<DatasetLabelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(DatasetLabelEntity::getDatasetId);
        wrapper.in(DatasetLabelEntity::getLabelId, labelIds);
        wrapper.groupBy(DatasetLabelEntity::getDatasetId);
        wrapper.having("COUNT(DISTINCT label_Id) = " + labelIds.size());
        List<DatasetLabelEntity> list = this.list(wrapper);
        // 取id
        List<String> datasetIds = list.stream().map(DatasetLabelEntity::getDatasetId).collect(Lists::newArrayList, List::add, List::addAll);
        return datasetIds;
    }

    @Override
    public List<String> getLabelIdsByDatasetId(String datasetId) {
        LambdaQueryWrapper<DatasetLabelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(DatasetLabelEntity::getLabelId);
        wrapper.eq(DatasetLabelEntity::getDatasetId, datasetId);
        List<DatasetLabelEntity> list = this.list(wrapper);
        // 取id，且去重
        List<String> labelIds = list.stream().map(DatasetLabelEntity::getLabelId).distinct().collect(Lists::newArrayList, List::add, List::addAll);
        return labelIds;
    }
}