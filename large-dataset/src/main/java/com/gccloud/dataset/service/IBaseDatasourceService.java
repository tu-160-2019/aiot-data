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
import com.gccloud.common.exception.GlobalException;
import com.gccloud.common.service.ISuperService;
import com.gccloud.common.vo.PageVO;
import com.gccloud.dataset.dto.DatasourceDTO;
import com.gccloud.dataset.dto.DatasourceSearchDTO;
import com.gccloud.dataset.entity.DatasourceEntity;
import com.gccloud.dataset.utils.DBUtils;
import com.gccloud.dataset.utils.DESUtils;
import com.gccloud.dataset.vo.DataVO;
import com.gccloud.dataset.vo.DeleteCheckVO;
import com.gccloud.dataset.vo.FieldInfoVO;
import com.gccloud.dataset.vo.TableInfoVO;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/2 10:44
 */
public interface IBaseDatasourceService extends ISuperService<DatasourceEntity> {

    /**
     * 分页列表
     *
     * @param searchDTO
     * @return
     */
    default PageVO<DatasourceEntity> getPage(DatasourceSearchDTO searchDTO) {
        LambdaQueryWrapper<DatasourceEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(searchDTO.getSourceName()), DatasourceEntity::getSourceName, searchDTO.getSourceName());
        queryWrapper.eq(StringUtils.isNotBlank(searchDTO.getSourceType()), DatasourceEntity::getSourceType, searchDTO.getSourceType());
        queryWrapper.orderByDesc(DatasourceEntity::getCreateDate);
        PageVO<DatasourceEntity> page = this.page(searchDTO, queryWrapper);
        page.getList().forEach(datasourceConfig -> datasourceConfig.setPassword(null));
        return page;
    }

    /**
     * 列表查询
     *
     * @param searchDTO
     * @return
     */
    default List<DatasourceEntity> getList(DatasourceSearchDTO searchDTO) {
        LambdaQueryWrapper<DatasourceEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(searchDTO.getSourceName()), DatasourceEntity::getSourceName, searchDTO.getSourceName());
        queryWrapper.eq(StringUtils.isNotBlank(searchDTO.getSourceType()), DatasourceEntity::getSourceType, searchDTO.getSourceType());
        queryWrapper.orderByDesc(DatasourceEntity::getCreateDate);
        List<DatasourceEntity> list = this.list(queryWrapper);
        list.forEach(datasourceConfig -> datasourceConfig.setPassword(null));
        return list;
    }

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    default String add(DatasourceDTO entity) {
        entity.setPassword(DESUtils.getEncryptString(entity.getPassword()));
        this.save(entity);
        return entity.getId();
    }


    /**
     * 修改
     *
     * @param entity
     * @return
     */
    default void update(DatasourceDTO entity) {
        if (StringUtils.isBlank(entity.getPassword())) {
            // 密码为空，不修改
            entity.setPassword(null);
            this.updateById(entity);
            return;
        }
        // 密码不为空，需要加密 TODO 这里需要前端控制，未修改密码时，不传密码
        entity.setPassword(DESUtils.getEncryptString(entity.getPassword()));
        this.updateById(entity);
    }

    /**
     * 删除
     * 该方法不会检查是否被数据集引用，如果需要检查，请再删除使用deleteCheck方法
     *
     * @param id
     * @return
     */
    default void delete(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }
        this.removeById(id);
    }

    /**
     * 删除前检查
     * 检查是否被数据集引用
     *
     * @param id
     * @return
     */
    default DeleteCheckVO deleteCheck(String id) {
        return new DeleteCheckVO();
    }


    /**
     * 根据id获取
     *
     * @param id
     * @return
     */
    default DatasourceEntity getInfoById(String id) {
        if (StringUtils.isBlank(id)) {
            throw new GlobalException("id不能为空");
        }
        DatasourceEntity entity = this.getById(id);
        if (entity == null) {
            throw new GlobalException("数据源不存在");
        }
        return entity;
    }

    /**
     * 数据源名称重复校验
     *
     * @param id
     * @param name
     * @return
     */
    default boolean checkNameRepeat(String id, String name) {
        LambdaQueryWrapper<DatasourceEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(DatasourceEntity::getId);
        queryWrapper.eq(DatasourceEntity::getSourceName, name);
        queryWrapper.ne(StringUtils.isNotBlank(id), DatasourceEntity::getId, id);
        return this.list(queryWrapper).size() > 0;
    }

    /**
     * 测试连接
     * 默认实现通过检查是否能够获取到连接来判断是否连接成功，实现类可以重写该方法
     *
     * @param datasource
     * @return
     */
    default String sourceLinkTest(DatasourceEntity datasource) {
        Connection connection = null;
        try {
            if (StringUtils.isEmpty(datasource.getId())) {
                // 新增时，密码尚未进行加密
                datasource.setPassword(DESUtils.getEncryptString(datasource.getPassword()));
            } else {
                DatasourceEntity entity = this.getInfoById(datasource.getId());
                if (StringUtils.isBlank(datasource.getPassword())) {
                    // 密码为空，则说明密码未修改，使用原密码
                    datasource.setPassword(entity.getPassword());
                }
                if (!entity.getPassword().equals(datasource.getPassword())) {
                    // 密码被修改，需要进行加密
                    datasource.setPassword(DESUtils.getEncryptString(datasource.getPassword()));
                }
            }
            connection = DBUtils.getConnection(datasource);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return "连接成功";
    }


    /**
     * 执行sql
     *
     * @param datasource
     * @param sql
     * @return
     */
    DataVO executeSql(DatasourceEntity datasource, String sql);


    /**
     * 执行sql分页
     *
     * @param datasource
     * @param sql
     * @param current
     * @param size
     * @return
     */
    default DataVO executeSqlPage(DatasourceEntity datasource, String sql, Integer current, Integer size) {
        // 分页方法不是必须实现的的,因为有些数据源可能不支持分页，默认调用不分页方法，如果支持分页，可以重写该方法
        return executeSql(datasource, sql);
    }

    /**
     * 执行存储过程
     *
     * @param datasource
     * @param procedure
     * @param current
     * @param size
     * @return
     */
    default DataVO executeProcedure(DatasourceEntity datasource, String procedure, Integer current, Integer size) {
        return null;
    }

    /**
     * 获取表列表
     *
     * @param datasource
     * @return
     */
    default List<TableInfoVO> getTableList(DatasourceEntity datasource) {
        return null;
    }


    /**
     * 获取表字段列表
     *
     * @param datasource
     * @param tableName
     * @return
     */
    default List<FieldInfoVO> getTableColumnList(DatasourceEntity datasource, String tableName) {
        return null;
    }

    /**
     * 视图列表
     *
     * @param datasource
     * @return
     */
    default List<TableInfoVO> getViewList(DatasourceEntity datasource) {
        return null;
    }


    /**
     * 获取视图字段列表
     *
     * @param datasource
     * @param viewName
     * @return
     */
    default List<FieldInfoVO> getViewColumnList(DatasourceEntity datasource, String viewName) {
        return null;
    }


}