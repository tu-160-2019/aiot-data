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

package com.gccloud.dataset.typehandler;

import com.gccloud.common.utils.JSON;
import com.gccloud.dataset.entity.config.BaseDataSetConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据集配置类转换器
 *
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 10:38
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(BaseDataSetConfig.class)
public class BaseDataSetConfigTypeHandler extends BaseTypeHandler<BaseDataSetConfig> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, BaseDataSetConfig obj, JdbcType jdbcType) throws SQLException {
        String data = JSON.toJSONString(obj);
        preparedStatement.setString(i, data);
    }

    @Override
    public BaseDataSetConfig getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        String data = resultSet.getString(columnName);
        if (StringUtils.isBlank(data)) {
            return null;
        }
        BaseDataSetConfig baseDatasetConfig = JSON.parseObject(data, BaseDataSetConfig.class);
        return baseDatasetConfig;
    }

    @Override
    public BaseDataSetConfig getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String data = resultSet.getString(i);
        if (StringUtils.isBlank(data)) {
            return null;
        }
        BaseDataSetConfig baseDatasetConfig = JSON.parseObject(data, BaseDataSetConfig.class);
        return baseDatasetConfig;
    }

    @Override
    public BaseDataSetConfig getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String data = callableStatement.getString(i);
        if (StringUtils.isBlank(data)) {
            return null;
        }
        BaseDataSetConfig baseDatasetConfig = JSON.parseObject(data, BaseDataSetConfig.class);
        return baseDatasetConfig;
    }
}