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
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gccloud.common.exception.GlobalException;
import com.gccloud.common.utils.BeanConvertUtils;
import com.gccloud.common.vo.PageVO;
import com.gccloud.dataset.dao.LabelDao;
import com.gccloud.dataset.dto.LabelDTO;
import com.gccloud.dataset.dto.LabelSearchDTO;
import com.gccloud.dataset.entity.DatasetLabelEntity;
import com.gccloud.dataset.entity.LabelEntity;
import com.gccloud.dataset.service.IDatasetLabelService;
import com.gccloud.dataset.service.ILabelService;
import com.gccloud.dataset.vo.DatasetLabelVO;
import com.gccloud.dataset.vo.LabelVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/7/5 10:43
 */
@Service
public class LabelServiceImpl extends ServiceImpl<LabelDao, LabelEntity> implements ILabelService {

    @Resource
    private IDatasetLabelService datasetLabelService;

    @Override
    public PageVO<LabelEntity> getPage(LabelSearchDTO searchDTO) {
        LambdaQueryWrapper<LabelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(searchDTO.getLabelName()), LabelEntity::getLabelName, searchDTO.getLabelName());
        wrapper.eq(StringUtils.isNotBlank(searchDTO.getLabelType()), LabelEntity::getLabelType, searchDTO.getLabelType());
        wrapper.select(LabelEntity::getId, LabelEntity::getLabelName, LabelEntity::getLabelType, LabelEntity::getLabelDesc);
        wrapper.orderByDesc(LabelEntity::getCreateDate);
        return this.page(searchDTO, wrapper);
    }

    @Override
    public String add(LabelDTO labelDTO) {
        // 校验标签名称是否重复
        LabelEntity labelEntity = BeanConvertUtils.convert(labelDTO, LabelEntity.class);
        boolean repeat = this.checkRepeat(labelEntity);
        if (repeat) {
            throw new GlobalException("标签名称重复");
        }
        // 新增标签
        this.save(labelEntity);
        return labelEntity.getId();
    }

    @Override
    public void update(LabelDTO labelDTO) {
        // 校验标签名称是否重复
        LabelEntity labelEntity = BeanConvertUtils.convert(labelDTO, LabelEntity.class);
        boolean repeat = this.checkRepeat(labelEntity);
        if (repeat) {
            throw new GlobalException("标签名称重复");
        }
        // 更新标签
        this.updateById(labelEntity);
    }

    @Override
    public void delete(String id) {
        // 先删除数据集与标签的关联
        datasetLabelService.deleteByLabelId(id);
        // 删除标签
        this.removeById(id);
    }


    @Override
    public LabelVO getInfoById(String id) {
        if (StringUtils.isBlank(id)) {
            throw new GlobalException("id不能为空");
        }
        LabelEntity labelEntity = this.getById(id);
        if (labelEntity == null) {
            throw new GlobalException("标签不存在");
        }
        List<DatasetLabelVO> dataSetList = datasetLabelService.getDatasetByLabelId(id);
        //定义标签初始化高度
        int y = 100;
        if (!CollectionUtils.isEmpty(dataSetList)) {
            if (dataSetList.size() % 2 == 0) {
                //偶数
                y = 100 * (dataSetList.size() / 2 + 1) - 50;
            } else {
                //奇数
                y = 100 * (dataSetList.size() / 2 + 1);
            }
        }

        Map<String, Object> dataSetMap = new HashMap<>();

        //节点集合
        List<Map<String, Object>> nodeList = new ArrayList<>(16);
        List<Map<String, Object>> edgeList = new ArrayList<>(16);

        //标签节点
        Map<String, Object> nodeMap = new HashMap<>();
        nodeMap.put("id", labelEntity.getId() + "_node");
        nodeMap.put("x", 400);
        nodeMap.put("y", y);
        nodeMap.put("class", "c3");
        nodeMap.put("label", labelEntity.getLabelName());
        nodeList.add(nodeMap);

        dataSetMap.put("nodes", nodeList);
        dataSetMap.put("edges", edgeList);
        LabelVO labelVO = BeanConvertUtils.convert(labelEntity, LabelVO.class);
        labelVO.setJsonData(dataSetMap);
        if (CollectionUtils.isEmpty(dataSetList)) {
            return labelVO;
        }
        // 将数据集添加至节点中
        int count = 1;
        for (DatasetLabelVO datasetLabelVO : dataSetList) {
            Map<String, Object> dataSetNodeMap = new HashMap<>();
            dataSetNodeMap.put("id", datasetLabelVO.getId());
            dataSetNodeMap.put("x", 800);
            dataSetNodeMap.put("y", count * 100);
            dataSetNodeMap.put("label", datasetLabelVO.getName());
            nodeList.add(dataSetNodeMap);
            count++;

            Map<String, Object> edgeMap = new HashMap<>();
            edgeMap.put("source", labelEntity.getId() + "_node");
            edgeMap.put("target", datasetLabelVO.getId());
            edgeList.add(edgeMap);
        }
        return labelVO;
    }

    @Override
    public boolean checkRepeat(LabelEntity labelEntity) {
        LambdaQueryWrapper<LabelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(LabelEntity::getId);
        wrapper.eq(LabelEntity::getLabelName, labelEntity.getLabelName());
        wrapper.ne(StringUtils.isNotBlank(labelEntity.getId()), LabelEntity::getId, labelEntity.getId());
        return this.list(wrapper).size() > 0;
    }

    @Override
    public List<String> getLabelType() {
        LambdaQueryWrapper<LabelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(LabelEntity::getLabelType);
        wrapper.groupBy(LabelEntity::getLabelType);
        return this.list(wrapper).stream().map(LabelEntity::getLabelType).collect(Collectors.toList());
    }


    @Override
    public void deleteLabelByType(String labelType) {
        LambdaQueryWrapper<LabelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LabelEntity::getLabelType, labelType);
        List<LabelEntity> list = this.list(wrapper);
        List<String> idList = list.stream().map(LabelEntity::getId).collect(Collectors.toList());
        if (idList.isEmpty()) {
            return;
        }
        // 先检查数据集与标签的关联
        LambdaQueryWrapper<DatasetLabelEntity> datasetLabelWrapper = new LambdaQueryWrapper<>();
        datasetLabelWrapper.select(DatasetLabelEntity::getLabelId);
        datasetLabelWrapper.in(DatasetLabelEntity::getLabelId, idList);
        if (datasetLabelService.list(datasetLabelWrapper).size() > 0) {
            throw new GlobalException("该标签类型下存在数据集与标签的关联，无法删除");
        }
        // 删除标签
        this.removeByIds(idList);
    }

    @Override
    public void updateLabelType(String newLabelType, String oldLabelType) {
        if (newLabelType.equals(oldLabelType)) {
            return;
        }
        LambdaQueryWrapper<LabelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(LabelEntity::getId);
        wrapper.eq(LabelEntity::getLabelType, newLabelType);
        wrapper.ne(LabelEntity::getLabelType, oldLabelType);
        if (this.list(wrapper).size() > 0) {
            throw new GlobalException("标签类型已存在");
        }
        LambdaUpdateWrapper<LabelEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(LabelEntity::getLabelType, newLabelType);
        updateWrapper.eq(LabelEntity::getLabelType, oldLabelType);
        this.update(updateWrapper);
    }
}