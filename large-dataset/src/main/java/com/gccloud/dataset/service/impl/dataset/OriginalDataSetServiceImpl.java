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

package com.gccloud.dataset.service.impl.dataset;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gccloud.common.exception.GlobalException;
import com.gccloud.common.utils.JSON;
import com.gccloud.common.vo.PageVO;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dao.DatasetDao;
import com.gccloud.dataset.dto.DatasetParamDTO;
import com.gccloud.dataset.dto.TestExecuteDTO;
import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.entity.DatasourceEntity;
import com.gccloud.dataset.entity.config.BaseDataSetConfig;
import com.gccloud.dataset.entity.config.OriginalDataSetConfig;
import com.gccloud.dataset.extend.dataset.DatasetExtendClient;
import com.gccloud.dataset.permission.DatasetPermissionClient;
import com.gccloud.dataset.service.IBaseDataSetService;
import com.gccloud.dataset.service.IBaseDatasourceService;
import com.gccloud.dataset.service.factory.DatasourceServiceFactory;
import com.gccloud.dataset.service.impl.datasource.BaseDatasourceServiceImpl;
import com.gccloud.dataset.vo.DataVO;
import com.gccloud.dataset.vo.DatasetInfoVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 原始数据集服务实现类
 *
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 11:20
 */
@Slf4j
@Service(DatasetConstant.DataSetType.ORIGINAL)
public class OriginalDataSetServiceImpl extends ServiceImpl<DatasetDao, DatasetEntity> implements IBaseDataSetService {

    @Resource
    private DatasourceServiceFactory datasourceServiceFactory;

    @Resource
    private BaseDatasourceServiceImpl datasourceService;

    @Resource
    private DatasetPermissionClient datasetPermissionClient;

    @Resource
    private DatasetExtendClient datasetExtendClient;

    @Override
    public String add(DatasetEntity entity) {
        OriginalDataSetConfig config = (OriginalDataSetConfig) entity.getConfig();
        entity.setCode(config.getTableName());
        String id = IBaseDataSetService.super.add(entity);
        if (datasetPermissionClient.hasPermissionService()) {
            // 添加数据集权限
            datasetPermissionClient.addPermission(id);
        }
        return id;
    }

    @Override
    public void delete(String id) {
        IBaseDataSetService.super.delete(id);
        if (datasetPermissionClient.hasPermissionService()) {
            // 删除数据集权限
            datasetPermissionClient.deletePermission(id);
        }
    }

    @Override
    public PageVO execute(String id, List<DatasetParamDTO> params, int current, int size) {
        if (StringUtils.isBlank(id)) {
            throw new GlobalException("数据集id不能为空");
        }
        DatasetEntity entity = this.getByIdFromCache(id);
        if (entity == null) {
            throw new GlobalException("数据集不存在");
        }
        long startTime = System.currentTimeMillis();
        OriginalDataSetConfig config = (OriginalDataSetConfig) entity.getConfig();
        StringBuilder fieldInfo = new StringBuilder(config.getFieldInfo());
        if (StringUtils.isBlank(fieldInfo.toString())) {
            List<Map<String, Object>> fieldList = config.getFieldList();
            for (Map<String, Object> field : fieldList) {
                String fieldName = (String) field.get(DatasetInfoVO.FIELD_NAME);
                fieldInfo.append(fieldName).append(",");
            }
            fieldInfo.deleteCharAt(fieldInfo.length() - 1);
        }
        String dataSourceId = config.getSourceId();
        DatasourceEntity datasource = datasourceService.getInfoById(dataSourceId);
        String sourceType = datasource.getSourceType();
        fieldInfo = new StringBuilder(handleSpecialField(fieldInfo.toString(), sourceType));
        if (DatasetConstant.DataRepeat.NOT_REPEAT.equals(config.getRepeatStatus())) {
            fieldInfo.insert(0, "DISTINCT ");
        }
        String sql = "SELECT " + fieldInfo + " FROM " + config.getTableName();
        IBaseDatasourceService buildService = datasourceServiceFactory.build(datasource.getSourceType());
        log.info("执行【{}】数据集（类型：【原始】，ID:【{}】）， URL：【{}】， 执行SQL：【{}】", entity.getName(), entity.getId(), datasource.getUrl(), sql);
        DataVO dataVO = buildService.executeSqlPage(datasource, sql, current, size);
        PageVO data = (PageVO) dataVO.getData();
        List list = data.getList();
        // 自定义数据处理
        list = datasetExtendClient.handleData(list, entity);
        data.setList(list);
        long endTime = System.currentTimeMillis();
        log.info("执行【{}】数据集（类型：【原始】，ID:【{}】），耗时：【{}】ms", entity.getName(), entity.getId(), endTime - startTime);
        return data;
    }

    @Override
    public Object execute(String id, List<DatasetParamDTO> params) {
        if (StringUtils.isBlank(id)) {
            throw new GlobalException("数据集id不能为空");
        }
        DatasetEntity entity = this.getByIdFromCache(id);
        if (entity == null) {
            throw new GlobalException("数据集不存在");
        }
        if (DatasetConstant.DatasetCache.OPEN.equals(entity.getCache())) {
            CompletableFuture<Object> future = DATASET_CACHE.get(id, key -> getData(entity));
            try {
                return future.get();
            } catch (Exception e) {
                log.error("数据集缓存异常：{}", e.getMessage());
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return getData(entity);
    }

    /**
     * 获取数据
     *
     * @param entity
     * @return
     */
    private Object getData(DatasetEntity entity) {
        long startTime = System.currentTimeMillis();
        OriginalDataSetConfig config = (OriginalDataSetConfig) entity.getConfig();
        String fieldInfo = config.getFieldInfo();
        if (StringUtils.isBlank(fieldInfo)) {
            List<Map<String, Object>> fieldList = config.getFieldList();
            StringBuilder fieldInfoBuilder = new StringBuilder();
            for (Map<String, Object> field : fieldList) {
                String fieldName = (String) field.get(DatasetInfoVO.FIELD_NAME);
                fieldInfoBuilder.append(fieldName).append(",");
            }
            fieldInfo = fieldInfoBuilder.deleteCharAt(fieldInfoBuilder.length() - 1).toString();
        }
        String dataSourceId = config.getSourceId();
        DatasourceEntity datasource = datasourceService.getInfoById(dataSourceId);
        String sourceType = datasource.getSourceType();
        fieldInfo = handleSpecialField(fieldInfo, sourceType);
        if (DatasetConstant.DataRepeat.NOT_REPEAT.equals(config.getRepeatStatus())) {
            fieldInfo = "DISTINCT " + fieldInfo;
        }
        String sql = "SELECT " + fieldInfo + " FROM " + config.getTableName();
        log.info("执行【{}】数据集（类型：【原始】，ID:【{}】）， URL：【{}】， 执行SQL：【{}】", entity.getName(), entity.getId(), datasource.getUrl(), sql);
        IBaseDatasourceService buildService = datasourceServiceFactory.build(sourceType);
        DataVO dataVO = buildService.executeSql(datasource, sql);
        List list = (List) dataVO.getData();
        // 自定义数据处理
        list = datasetExtendClient.handleData(list, entity);
        long endTime = System.currentTimeMillis();
        log.info("执行【{}】数据集（类型：【原始】，ID:【{}】），耗时：【{}】ms", entity.getName(), entity.getId(), endTime - startTime);
        return list;
    }

    @Override
    public DataVO execute(TestExecuteDTO executeDTO) {
        String jsonString = executeDTO.getScript();
        if (StringUtils.isBlank(jsonString)) {
            throw new GlobalException("数据集配置异常");
        }
        long startTime = System.currentTimeMillis();
        JSONObject originalTest = JSON.parseObject(jsonString);
        JSONArray fieldArray = originalTest.getJSONArray("fieldInfo");
        // 逗号分隔
        String fieldInfo = "";
        for (Object o : fieldArray) {
            String field = (String) o;
            fieldInfo += field + ",";
        }
        if (StringUtils.isBlank(fieldInfo)) {
            fieldInfo = "*";
        } else {
            fieldInfo = fieldInfo.substring(0, fieldInfo.length() - 1);
        }
        String dataSourceId = executeDTO.getDataSourceId();
        DatasourceEntity datasource = datasourceService.getInfoById(dataSourceId);
        String sourceType = datasource.getSourceType();
        fieldInfo = handleSpecialField(fieldInfo, sourceType);
        String sql = "SELECT ";
        if (DatasetConstant.DataRepeat.NOT_REPEAT.equals(originalTest.getInt("repeatStatus"))) {
            sql += "DISTINCT ";
        }
        sql += fieldInfo + " FROM " + originalTest.getString("tableName");
        IBaseDatasourceService buildService = datasourceServiceFactory.build(datasource.getSourceType());
        DataVO dataVO;
        Integer current = executeDTO.getCurrent();
        Integer size = executeDTO.getSize();
        log.info("测试数据集（类型：【原始】）， URL：【{}】， 执行SQL：【{}】", datasource.getUrl(), sql);
        if (size != null && current != null) {
            dataVO = buildService.executeSqlPage(datasource, sql, current, size);
        } else {
            dataVO = buildService.executeSql(datasource, sql);
        }
        long endTime = System.currentTimeMillis();
        log.info("测试数据集（类型：【原始】）结束，耗时：【{}】ms", endTime - startTime);
        return dataVO;
    }

    /**
     * 处理特殊字段
     *
     * @param fieldInfo
     * @param sourceType
     * @return
     */
    private String handleSpecialField(String fieldInfo, String sourceType) {
        if ("*".equals(fieldInfo)) {
            return fieldInfo;
        }
        // 分割字段，TODO 这里可能存在一个问题，如果字段中有逗号，会被分割成多个字段
        List<String> fieldList = Lists.newArrayList(fieldInfo.split(","));
        StringBuilder fields = new StringBuilder();
        // 遍历处理字段
        for (String field : fieldList) {
            // 如果字段中包含除数字、字母、下划线以外的字符，需要特殊处理
            if (field.matches("^[a-zA-Z0-9_]+$")) {
                fields.append(field).append(",");
                continue;
            }
            // 如果已经是特殊处理的字段，直接添加到字段列表中
            if (field.startsWith("`") && field.endsWith("`")) {
                fields.append(field).append(",");
                continue;
            }
            if (field.startsWith("\"") && field.endsWith("\"")) {
                fields.append(field).append(",");
                continue;
            }
            switch (sourceType.toLowerCase()) {
                case DatasetConstant.DatasourceType.MYSQL:
                case DatasetConstant.DatasourceType.CLICKHOUSE:
                    field = field.replace(field, "`" + field + "`");
                    break;
                case DatasetConstant.DatasourceType.ORACLE:
                case DatasetConstant.DatasourceType.POSTGRESQL:
                    field = field.replace(field, "\"" + field + "\"");
                    break;
                default:
                    break;
            }
            fields.append(field).append(",");
        }
        return fields.substring(0, fields.length() - 1);
    }

    /**
     * 根据数据源id和表名获取数据集列表
     *
     * @param tableName
     * @param sourceId
     * @return
     */
    public List<DatasetEntity> getListByTableName(String tableName, String sourceId) {
        LambdaQueryWrapper<DatasetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DatasetEntity::getDatasetType, DatasetConstant.DataSetType.ORIGINAL);
        queryWrapper.eq(DatasetEntity::getSourceId, sourceId);
        // 原始数据集的code就是表名
        queryWrapper.like(DatasetEntity::getCode, tableName);
        List<DatasetEntity> list = this.list(queryWrapper);
        // 遍历二次过滤
        list.removeIf(datasetEntity -> {
            BaseDataSetConfig config = datasetEntity.getConfig();
            if (!(config instanceof OriginalDataSetConfig)) {
                return true;
            }
            OriginalDataSetConfig originalConfig = (OriginalDataSetConfig) config;
            if (originalConfig.getSourceId().equals(sourceId) && originalConfig.getTableName().equals(tableName)) {
                return false;
            }
            return true;
        });
        return list;
    }
}