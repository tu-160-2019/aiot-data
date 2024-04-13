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

package com.gccloud.dataset.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gccloud.dataset.entity.DatasetLabelEntity;
import com.gccloud.dataset.entity.LabelEntity;
import com.gccloud.dataset.vo.DatasetLabelVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/7/5 10:30
 */
@Mapper
public interface DatasetLabelDao extends BaseMapper<DatasetLabelEntity> {

    /**
     * 根据标签id获取数据集信息
     *
     * @param labelId
     * @return
     */
    @Select("SELECT d.id as id, d.name as name FROM ds_dataset d LEFT JOIN ds_dataset_label r ON d.id = r.dataset_id WHERE r.label_id = #{labelId}")
    List<DatasetLabelVO> getDatasetByLabelId(String labelId);


    /**
     * 根据数据集id获取标签信息
     *
     * @param datasetId
     * @return
     */
    @Select("SELECT l.id as id, l.label_name as labelName FROM ds_label l LEFT JOIN ds_dataset_label r ON l.id = r.label_id WHERE r.dataset_id = #{datasetId}")
    List<LabelEntity> getLabelByDatasetId(String datasetId);

}