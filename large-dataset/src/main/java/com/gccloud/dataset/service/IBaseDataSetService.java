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

package com.gccloud.dataset.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gccloud.common.exception.GlobalException;
import com.gccloud.common.service.ISuperService;
import com.gccloud.common.utils.BeanConvertUtils;
import com.gccloud.common.vo.PageVO;
import com.gccloud.dataset.dto.DatasetParamDTO;
import com.gccloud.dataset.dto.DatasetSearchDTO;
import com.gccloud.dataset.dto.TestExecuteDTO;
import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.entity.config.BaseDataSetConfig;
import com.gccloud.dataset.vo.DataVO;
import com.gccloud.dataset.vo.DatasetInfoVO;
import com.gccloud.dataset.vo.DeleteCheckVO;
import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 10:56
 */
public interface IBaseDataSetService extends ISuperService<DatasetEntity> {

    /**
     * 数据集结果缓存
     */
    AsyncCache<String, Object> DATASET_CACHE = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).buildAsync();

    /**
     * 数据集实体缓存
     */
    AsyncCache<String, DatasetEntity> DATASET_ENTITY_CACHE = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).buildAsync();

    /**
     * 数据集配置缓存
     */
    AsyncCache<String, DatasetInfoVO> DATASET_INFO_CACHE = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).buildAsync();

    /**
     * 列表查询
     *
     * @param searchDTO
     * @return
     */
    default List<DatasetEntity> getList(DatasetSearchDTO searchDTO) {
        return this.list(this.getQueryWrapper(searchDTO));
    }

    /**
     * 分页查询
     *
     * @param searchDTO
     * @return
     */
    default PageVO<DatasetEntity> getPage(DatasetSearchDTO searchDTO) {
        return this.page(searchDTO, this.getQueryWrapper(searchDTO));
    }

    /**
     * 数据集查询的条件组装
     *
     * @param searchDTO
     * @return
     */
    default LambdaQueryWrapper<DatasetEntity> getQueryWrapper(DatasetSearchDTO searchDTO) {
        LambdaQueryWrapper<DatasetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(searchDTO.getName()), DatasetEntity::getName, searchDTO.getName());
        queryWrapper.eq(StringUtils.isNotBlank(searchDTO.getTypeId()), DatasetEntity::getTypeId, searchDTO.getTypeId());
        queryWrapper.in(CollectionUtils.isNotEmpty(searchDTO.getDatasetType()), DatasetEntity::getDatasetType, searchDTO.getDatasetType());
        queryWrapper.eq(StringUtils.isNotBlank(searchDTO.getSourceId()), DatasetEntity::getSourceId, searchDTO.getSourceId());
        if (searchDTO.getDatasetIds() != null && searchDTO.getDatasetIds().size() > 0) {
            queryWrapper.in(DatasetEntity::getId, searchDTO.getDatasetIds());
        }
        queryWrapper.orderByDesc(DatasetEntity::getUpdateDate);
        return queryWrapper;
    }

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    default String add(DatasetEntity entity) {
        if (StringUtils.isBlank(entity.getCode())) {
            // 随机生成编码，纯字母
            entity.setCode(RandomStringUtils.randomAlphabetic(10));
        }
        this.save(entity);
        return entity.getId();
    }


    /**
     * 修改
     *
     * @param entity
     * @return
     */
    default void update(DatasetEntity entity) {
        this.updateById(entity);
        DATASET_CACHE.synchronous().invalidate(entity.getId());
        DATASET_ENTITY_CACHE.synchronous().invalidate(entity.getId());
        DATASET_INFO_CACHE.synchronous().invalidate(entity.getId());
    }


    /**
     * 删除
     *
     * @param id
     * @return
     */
    default void delete(String id) {
        this.removeById(id);
        DATASET_CACHE.synchronous().invalidate(id);
        DATASET_ENTITY_CACHE.synchronous().invalidate(id);
        DATASET_INFO_CACHE.synchronous().invalidate(id);
    }


    /**
     * 删除前检查
     * 检查是否被引用
     *
     * @param id
     * @return
     */
    default DeleteCheckVO deleteCheck(String id) {
        return new DeleteCheckVO();
    }


    /**
     * 名称重复校验
     *
     * @param id
     * @param name
     * @return
     */
    default boolean checkNameRepeat(String id, String name) {
        LambdaQueryWrapper<DatasetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(DatasetEntity::getId);
        queryWrapper.eq(DatasetEntity::getName, name);
        queryWrapper.ne(StringUtils.isNotBlank(id), DatasetEntity::getId, id);
        return this.list(queryWrapper).size() > 0;
    }


    /**
     * 根据id查询数据集详情
     * 使用缓存减少数据库查询
     *
     * @param id
     * @return
     */
    default DatasetInfoVO getInfoById(String id) {
        CompletableFuture<DatasetInfoVO> future = DATASET_INFO_CACHE.get(id, key -> {
            DatasetEntity entity = this.getByIdFromCache(id);
            DatasetInfoVO datasetInfoVO = BeanConvertUtils.convert(entity, DatasetInfoVO.class);
            BaseDataSetConfig config = entity.getConfig();
            datasetInfoVO.setFields(config.getFieldList());
            datasetInfoVO.setParams(config.getParamsList());
            return datasetInfoVO;
        });
        try {
            return future.get();
        } catch (Exception ignored) {
        }
        DatasetEntity entity = this.getByIdFromCache(id);
        DatasetInfoVO datasetInfoVO = BeanConvertUtils.convert(entity, DatasetInfoVO.class);
        BaseDataSetConfig config = entity.getConfig();
        datasetInfoVO.setFields(config.getFieldList());
        datasetInfoVO.setParams(config.getParamsList());
        return datasetInfoVO;
    }

    /**
     * 根据id查询数据集详情
     * 使用缓存减少数据库查询
     *
     * @param id
     * @return
     */
    default DatasetEntity getByIdFromCache(String id) {
        CompletableFuture<DatasetEntity> future = DATASET_ENTITY_CACHE.get(id, key -> {
            DatasetEntity entity = this.getById(id);
            return entity;
        });
        try {
            return future.get();
        } catch (Exception ignored) {
        }
        DatasetEntity entity = this.getById(id);
        DATASET_ENTITY_CACHE.put(id, CompletableFuture.completedFuture(entity));
        return entity;
    }

    /**
     * 根据code查询数据集详情
     *
     * @param code
     * @return
     */
    default DatasetEntity getByCode(String code) {
        LambdaQueryWrapper<DatasetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DatasetEntity::getCode, code);
        List<DatasetEntity> list = this.list(queryWrapper);
        if (list.isEmpty()) {
            throw new GlobalException("数据集不存在");
        }
        if (list.size() > 1) {
            throw new GlobalException("数据集编码重复");
        }
        return list.get(0);
    }

    /**
     * 检查是否需要由后端执行
     *
     * @param datasetId
     * @return
     */
    default boolean checkBackendExecutionNeeded(String datasetId) {
        return true;
    }


    /**
     * 获取分页数据（数据集执行）
     *
     * @param id
     * @param params
     * @param current
     * @param size
     * @return
     */
    default PageVO<Object> execute(String id, List<DatasetParamDTO> params, int current, int size) {
        return null;
    }


    /**
     * 获取数据（数据集执行）
     *
     * @param datasetId
     * @param params
     * @return
     */
    Object execute(String datasetId, List<DatasetParamDTO> params);


    /**
     * 数据集执行测试
     * 在数据集新增、更新前需要先通过测试，确保数据集可用
     *
     * @param executeDTO
     * @return
     */
    default DataVO execute(TestExecuteDTO executeDTO) {
        return null;
    }


}