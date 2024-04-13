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

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gccloud.common.exception.GlobalException;
import com.gccloud.common.utils.GroovyUtils;
import com.gccloud.common.utils.JSON;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dao.DatasetDao;
import com.gccloud.dataset.dto.DatasetParamDTO;
import com.gccloud.dataset.dto.TestExecuteDTO;
import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.entity.config.GroovyDataSetConfig;
import com.gccloud.dataset.params.ParamsClient;
import com.gccloud.dataset.permission.DatasetPermissionClient;
import com.gccloud.dataset.service.IBaseDataSetService;
import com.gccloud.dataset.vo.DataVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 11:20
 */
@Slf4j
@Service(DatasetConstant.DataSetType.SCRIPT)
public class GroovyDataSetServiceImpl extends ServiceImpl<DatasetDao, DatasetEntity> implements IBaseDataSetService {

    @Resource
    private ParamsClient paramsClient;

    @Resource
    private DatasetPermissionClient datasetPermissionClient;


    @Override
    public String add(DatasetEntity entity) {
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
    public Object execute(String id, List<DatasetParamDTO> params) {
        if (StringUtils.isBlank(id)) {
            throw new GlobalException("数据集id不能为空");
        }
        final List<DatasetParamDTO> finalParams = params;
        DatasetEntity datasetEntity = this.getByIdFromCache(id);
        if (datasetEntity == null) {
            throw new GlobalException("数据集不存在");
        }
        if (DatasetConstant.DatasetCache.OPEN.equals(datasetEntity.getCache())) {
            CompletableFuture<Object> future = DATASET_CACHE.get(id, key -> getData(finalParams, datasetEntity));
            try {
                return future.get();
            } catch (Exception e) {
                log.error("数据集缓存异常：{}", e.getMessage());
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return getData(finalParams, datasetEntity);

    }

    /**
     * 获取数据
     *
     * @param finalParams
     * @param datasetEntity
     * @return
     */
    private Object getData(List<DatasetParamDTO> finalParams, DatasetEntity datasetEntity) {
        long startTime = System.currentTimeMillis();
        GroovyDataSetConfig config = (GroovyDataSetConfig) datasetEntity.getConfig();
        String script = config.getScript();
        // 参数预处理
        List<DatasetParamDTO> paramList = paramsClient.handleParams(finalParams);
        Map<String, Object> paramMap = this.buildParams(paramList, script);
        log.info("执行【{}】数据集（类型：【脚本】，ID:【{}】）， 参数：【{}】，", datasetEntity.getName(), datasetEntity.getId(), JSON.toJSONString(finalParams));
        Object run = GroovyUtils.run(script, paramMap);
        long endTime = System.currentTimeMillis();
        log.info("执行【{}】数据集（类型：【脚本】，ID:【{}】）结束，耗时：【{}】ms", datasetEntity.getName(), datasetEntity.getId(), endTime - startTime);
        return run;
    }


    @Override
    public DataVO execute(TestExecuteDTO executeDTO) {
        String script = executeDTO.getScript();
        if (StringUtils.isBlank(script)) {
            throw new GlobalException("脚本不能为空");
        }
        long startTime = System.currentTimeMillis();
        List<DatasetParamDTO> params = executeDTO.getParams();
        // 参数预处理
        params = paramsClient.handleParams(params);
        Map<String, Object> paramMap = this.buildParams(params, script);
        DataVO dataVO = new DataVO();
        log.info("测试数据集（类型：【脚本】）， 参数：【{}】， 执行脚本：【{}】", JSON.toJSONString(params), script);
        dataVO.setData(GroovyUtils.run(script, paramMap));
        long endTime = System.currentTimeMillis();
        log.info("测试数据集（类型：【脚本】）结束，耗时：【{}】ms", endTime - startTime);
        return dataVO;
    }


    /**
     * 构建参数，并且编译脚本
     *
     * @param params
     * @param script
     * @return
     */
    private Map<String, Object> buildParams(List<DatasetParamDTO> params, String script) {
        Map<String, Object> paramMap = new HashMap<>(16);
        if (!CollectionUtils.isEmpty(params)) {
            params.forEach(p -> paramMap.put(p.getName(), p.getValue()));
        }
        Class clazz = GroovyUtils.buildClass(script);
        if (clazz == null) {
            throw new GlobalException("脚本编译异常");
        }
        return paramMap;
    }
}