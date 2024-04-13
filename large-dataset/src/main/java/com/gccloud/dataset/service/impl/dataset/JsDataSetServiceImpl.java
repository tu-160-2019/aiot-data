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
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dao.DatasetDao;
import com.gccloud.dataset.dto.DatasetParamDTO;
import com.gccloud.dataset.dto.TestExecuteDTO;
import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.entity.config.JsDataSetConfig;
import com.gccloud.dataset.permission.DatasetPermissionClient;
import com.gccloud.dataset.service.IBaseDataSetService;
import com.gccloud.dataset.vo.DataVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * json数据集服务实现类
 *
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 11:20
 */
@Slf4j
@Service(DatasetConstant.DataSetType.JS)
public class JsDataSetServiceImpl extends ServiceImpl<DatasetDao, DatasetEntity> implements IBaseDataSetService {

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
        DatasetEntity datasetEntity = this.getByIdFromCache(id);
        if (datasetEntity == null) {
            throw new GlobalException("数据集不存在");
        }
        log.info("执行【{}】数据集（类型：【JS】，ID:【{}】）", datasetEntity.getName(), datasetEntity.getId());
        return datasetEntity.getConfig();
    }

    @Override
    public DataVO execute(TestExecuteDTO executeDTO) {
        String js = executeDTO.getScript();
        if (StringUtils.isBlank(js)) {
            throw new GlobalException("js脚本不能为空");
        }
        DataVO dataVO = new DataVO();
        dataVO.setData(js);
        return dataVO;
    }

    @Override
    public boolean checkBackendExecutionNeeded(String datasetId) {
        return false;
    }
}