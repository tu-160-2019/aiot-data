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

package com.gccloud.dataset.constant;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 11:39
 */
public interface DatasetConstant {


    interface DataSetType {
        /**
         * 自助数据集
         */
        String CUSTOM = "custom";
        /**
         * 原始数据集
         */
        String ORIGINAL = "original";
        /**
         * 脚本数据集
         */
        String SCRIPT = "script";
        /**
         * JSON数据集
         */
        String JSON = "json";
        /**
         * 存储过程数据集
         */
        String STORED_PROCEDURE = "storedProcedure";
        /**
         * JS数据集
         */
        String JS = "js";

        /**
         * HTTP数据集
         */
        String HTTP = "http";
    }

    interface DatasourceType {
        /**
         * mysql
         */
        String MYSQL = "mysql";

        /**
         * oracle
         */
        String ORACLE = "oracle";

        /**
         * sqlserver
         */
        String SQLSERVER = "sqlserver";

        /**
         * postgresql
         */
        String POSTGRESQL = "postgresql";

        /**
         * hive
         */
        String HIVE = "hive";

        /**
         * clickhouse
         */
        String CLICKHOUSE = "clickhouse";

        /**
         * excel
         */
        String EXCEL = "excel";

    }

    /**
     * 数据集缓存状态
     */
    interface DatasetCache {

        /**
         * 数据集开启缓存
         */
        Integer OPEN = 1;


        /**
         * 数据集关闭缓存
         */
        Integer CLOSE = 0;
    }

    /**
     * 数据是否去重
     */
    interface DataRepeat {
        /**
         * 不允许重复，即去重
         */
        Integer NOT_REPEAT = 1;
        /**
         * 默认 允许重复
         */
        Integer DEFAULT = 0;
    }

    /**
     * 语法类型
     * 自助数据集中的语法类型
     */
    interface SyntaxType {
        /**
         * 普通
         */
        String NORMAL = "normal";
        /**
         * mybatis
         */
        String MYBATIS = "mybatis";
    }

    /**
     * SQL中的参数类型
     */
    interface SqlParamsType {

        /**
         * 字符串参数，在替换sql中参数值时，会固定在值两侧加上单引号
         */
        String STRING = "String";

        /**
         * 日期参数
         */
        String DATE = "Date";

        /**
         * 整型
         */
        String INTEGER = "Integer";

        /**
         * 长整型
         */
        String LONG = "Long";

        /**
         * 浮点型
         */
        String DOUBLE = "Double";

    }

    /**
     * SQL中的参数状态
     */
    interface SqlParamsStatus {
        /**
         * 默认
         */
        Integer DEFAULT = 0;
        /**
         * 变量
         */
        Integer VARIABLE = 1;
    }

    /**
     * 扫描包路径
     */
    interface ScanPackage {

        /**
         * 基础包路径
         */
        String COMPONENT = "com.gccloud.dataset";

        /**
         * dao路径
         */
        String DAO = "com.gccloud.dataset.**.dao";

    }


    /**
     * 接口权限标识
     */
    interface Permission {

        /**
         * 数据源
         */
        interface Datasource {
            /**
             * 数据源的查询接口权限
             */
            String VIEW = "datasource:view";

            /**
             * 数据源的添加接口权限
             */
            String ADD = "datasource:add";

            /**
             * 数据源的编辑接口权限
             */
            String UPDATE = "datasource:update";

            /**
             * 数据源的删除接口权限
             */
            String DELETE = "datasource:delete";

            /**
             * 数据源的测试接口权限
             */
            String TEST = "datasource:test";
        }

        /**
         * 数据集
         */
        interface Dataset {

            /**
             * 数据集的查询接口权限
             */
            String VIEW = "dataset:view";

            /**
             * 数据集的添加接口权限
             */
            String ADD = "dataset:add";

            /**
             * 数据集的编辑接口权限
             */
            String UPDATE = "dataset:update";


            /**
             * 数据集的删除接口权限
             */
            String DELETE = "dataset:delete";

            /**
             * 数据集的执行（获取数据）接口权限
             */
            String EXECUTE = "dataset:execute";

            /**
             * 数据集的分类树相关查询接口权限
             */
            String CATEGORY_VIEW = "dataset:category";

            /**
             * 数据集的分类树相关操作接口权限（增删改）
             */
            String CATEGORY_EDIT = "dataset:category:edit";

            /**
             * 数据集标签相关查询接口权限
             */
            String LABEL_VIEW = "dataset:label";

            /**
             * 数据集标签相关操作接口权限（增删改）
             */
            String LABEL_EDIT = "dataset:label:edit";
        }

    }

}