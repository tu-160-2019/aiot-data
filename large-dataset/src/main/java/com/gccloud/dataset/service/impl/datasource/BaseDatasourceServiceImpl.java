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

package com.gccloud.dataset.service.impl.datasource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gccloud.dataset.dao.DatasourceDao;
import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.entity.DatasourceEntity;
import com.gccloud.dataset.extend.datasource.DatasourceExtendClient;
import com.gccloud.dataset.service.IBaseDatasourceService;
import com.gccloud.dataset.service.impl.dataset.BaseDatasetServiceImpl;
import com.gccloud.dataset.vo.DataVO;
import com.gccloud.dataset.vo.DeleteCheckVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 数据源基础服务，仅提供数据源的基础操作（增、删、改、查），未实现具体的数据源执行逻辑
 *
 * @author hongyang
 * @version 1.0
 * @date 2023/6/2 11:06
 */
@Primary
@Service("baseDatasourceService")
public class BaseDatasourceServiceImpl extends ServiceImpl<DatasourceDao, DatasourceEntity> implements IBaseDatasourceService {

    @Resource
    private DatasourceExtendClient extendClient;

    @Resource
    private BaseDatasetServiceImpl datasetService;

    @Override
    public DataVO executeSql(DatasourceEntity datasource, String sql) {
        return null;
    }


    @Override
    public DeleteCheckVO deleteCheck(String id) {
        LambdaQueryWrapper<DatasetEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(DatasetEntity::getId, DatasetEntity::getName);
        queryWrapper.eq(DatasetEntity::getSourceId, id);
        List<DatasetEntity> list = datasetService.list(queryWrapper);
        String msg = "";
        if (list != null && list.size() > 0) {
            msg = "数据源已被以下数据集引用，无法删除：";
            for (DatasetEntity dataset : list) {
                msg += dataset.getName() + "、";
            }
            msg = msg.substring(0, msg.length() - 1);
        }
        Map<String, String> reasons = extendClient.deleteCheck(id);
        if (StringUtils.isNotBlank(msg)) {
            reasons.put("数据集", msg);
        }
        DeleteCheckVO vo = new DeleteCheckVO();
        vo.setReasons(reasons);
        vo.setCanDelete(reasons.size() == 0);
        return vo;
    }
}