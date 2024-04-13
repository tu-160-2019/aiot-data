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

package com.gccloud.dataset.utils;

import com.gccloud.common.exception.GlobalException;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dto.DatasetParamDTO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 动态sql构造
 * 替换sql中的参数，并根据mybatis的规则，构造成sql语句（如：${}、#{}、<if test>、<where>等）
 *
 * @author hongyang
 * @version 1.0
 * @date 2023/10/23 13:47
 */
@Slf4j
@Component
public class MybatisParameterUtils {

    @Resource
    private SqlSessionFactory sqlSessionFactory;

    private SqlBuilderStatement sqlBuilderStatement;

    @PostConstruct
    public void init() {
        this.sqlBuilderStatement = new SqlBuilderStatement(sqlSessionFactory.getConfiguration());
    }


    /**
     * mybatis规则关键字
     */
    public static final List<String> MYBATIS_KEYWORDS = Lists.newArrayList("where", "set", "choose", "when", "otherwise", "if", "trim", "foreach", "bind", "sql", "include");


    /**
     * sql构造
     * 按照mybatis的规则，进行sql替换
     *
     * @param sql    动态sql语句
     * @param params 参数配置
     */
    public String updateParamsConfig(String sql, List<DatasetParamDTO> params) {
        if (params == null) {
            params = new ArrayList<>();
        }
        // 检查是否包含${}、#{}
        boolean hasMybatisKeyword = sql.contains("${") || sql.contains("#{");
        // 检查是否包含mybatis标签
        for (String keyword : MYBATIS_KEYWORDS) {
            if (sql.contains("<" + keyword) || sql.contains("</" + keyword)) {
                hasMybatisKeyword = true;
                break;
            }
        }
        // 检查是否包含参数标签，有的话，顺便替换成mybatis标签
        for (DatasetParamDTO param : params) {
            String name = param.getName();
            if (sql.contains("<" + name + ">") && sql.contains("</" + name + ">")) {
                hasMybatisKeyword = true;
                // 兼容旧版本语法，将<参数名称>xxxx</参数名称>替换成<if test="参数名称 != null and 参数名称 != ''">xxx</if>  如果参数类型不是字符串的话，就不需要加''了
                if (param.getType().equals(DatasetConstant.SqlParamsType.STRING) || param.getType().equals(DatasetConstant.SqlParamsType.DATE)) {
                    sql = sql.replace("<" + name + ">", "<if test=\"" + name + " != null and " + name + " != ''\">");
                } else {
                    sql = sql.replace("<" + name + ">", "<if test=\"" + name + " != null\">");
                }
                sql = sql.replace("</" + name + ">", "</if>");
            }
        }
        // 如果不包含这些关键字，就不需要进行sql构造
        if (!hasMybatisKeyword) {
            return sql;
        }
        log.info("开始进行动态sql构造：{}", sql);
        // 构造sql前，需要将除了标签中的<、> 替换成 &lt;、&gt;，否则会被mybatis解析成标签
        Map<String, String> labelMap = new HashMap<>();
        // 将sql中的mybatis标签全部取出来，放到一个list中,原先的位置用占位符替换：mybatisLabel0
        int index = 0;
        for (String keyword : MYBATIS_KEYWORDS) {
            // 先取<keyword xxxxx>
            String regex = "<" + keyword + "[^>]*>";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(sql);
            while (matcher.find()) {
                index++;
                String group = matcher.group();
                String key = "[mybatisLabel" + index + "]";
                labelMap.put(key, group);
                sql = sql.replace(group, key);
            }
            // 再取</keyword>
            regex = "</" + keyword + ">";
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(sql);
            while (matcher.find()) {
                index++;
                String group = matcher.group();
                String key = "[mybatisLabel" + index + "]";
                labelMap.put(key, group);
                sql = sql.replace(group, key);
            }
        }
        // 剩余的<、>替换成 &lt;、&gt;
        sql = sql.replace("<", "&lt;").replace(">", "&gt;");
        // 再将mybatis标签替换回来
        for (Map.Entry<String, String> entry : labelMap.entrySet()) {
            sql = sql.replace(entry.getKey(), entry.getValue());
        }
        // 用到了mybatis语法，检查sql首尾是否添加了<script></script>标签，如果没有，添加上
        if (!sql.startsWith("<script>")) {
            sql = "<script>" + sql;
        }
        if (!sql.endsWith("</script>")) {
            sql = sql + "</script>";
        }
        Map<String, Object> parameterMap = new HashMap<>();
        for (DatasetParamDTO param : params) {
            if (null == param.getStatus()) {
                continue;
            }
            if (!DatasetConstant.SqlParamsStatus.VARIABLE.equals(param.getStatus())) {
                continue;
            }
            // 尝试按照参数类型进行强转，日期类的当成字符串处理
            String type = param.getType();
            Object value;
            switch (type) {
                case DatasetConstant.SqlParamsType.INTEGER:
                    try {
                        value = Integer.parseInt(param.getValue());
                    } catch (NumberFormatException e) {
                        log.error("参数{}转换成Integer类型失败", param.getName());
                        value = param.getValue();
                    }
                    break;
                case DatasetConstant.SqlParamsType.LONG:
                    try {
                        value = Long.parseLong(param.getValue());
                    } catch (NumberFormatException e) {
                        log.error("参数{}转换成Long类型失败", param.getName());
                        value = param.getValue();
                    }
                    break;
                case DatasetConstant.SqlParamsType.DOUBLE:
                    try {
                        value = Double.parseDouble(param.getValue());
                    } catch (NumberFormatException e) {
                        log.error("参数{}转换成Double类型失败", param.getName());
                        value = param.getValue();
                    }
                    break;
                case DatasetConstant.SqlParamsType.STRING:
                case DatasetConstant.SqlParamsType.DATE:
                default:
                    value = param.getValue();
                    break;
            }
            parameterMap.put(param.getName(), value);
        }
        String finalSql;
        List<ParameterMapping> parameterMappings;
        Map parameterObject;
        Class<?> parameterType = parameterMap.getClass();
        String msId = this.sqlBuilderStatement.selectDynamic(sql, parameterType);
        try (SqlSession sqlSession = sqlSessionFactory.openSession(true)) {
            MappedStatement mappedStatement = sqlSession.getConfiguration().getMappedStatement(msId);
            SqlSource sqlSource = mappedStatement.getSqlSource();
            finalSql = sqlSource.getBoundSql(parameterMap).getSql();
            // 获取sql中的?对应的参数
            parameterMappings = sqlSource.getBoundSql(parameterMap).getParameterMappings();
            parameterObject = (Map) sqlSource.getBoundSql(parameterMap).getParameterObject();
            // 如果没有参数，直接返回构造后的sql
            if (parameterMappings == null) {
                log.info("构造后的sql:{}", finalSql);
                return finalSql;
            }
            // 替换sql中的?为参数，这里只处理#{}的参数，${}已经被直接替换了
            for (ParameterMapping mapping : parameterMappings) {
                String property = mapping.getProperty();
                Object o = parameterObject.get(property);
                if (o instanceof String) {
                    finalSql = finalSql.replaceFirst("\\?", "'" + o + "'");
                } else {
                    finalSql = finalSql.replaceFirst("\\?", o.toString());
                }
            }
        } catch (Exception e) {
            log.error("构造sql失败:{}", sql);
            log.error(ExceptionUtils.getStackTrace(e));
            throw new GlobalException("构造sql失败");
        }
        // 移除多余的空格
        finalSql = removeExtraWhitespaces(finalSql);
        log.info("构造后的sql:{}", finalSql);
        return finalSql;
    }


    /**
     * 移除字符串中多余的空格
     *
     * @param original
     * @return
     */
    public String removeExtraWhitespaces(String original) {
        StringTokenizer tokenizer = new StringTokenizer(original);
        StringBuilder builder = new StringBuilder();
        boolean hasMoreTokens = tokenizer.hasMoreTokens();
        while (hasMoreTokens) {
            builder.append(tokenizer.nextToken());
            hasMoreTokens = tokenizer.hasMoreTokens();
            if (hasMoreTokens) {
                builder.append(' ');
            }
        }
        return builder.toString();
    }

    /**
     * sql构建器
     */
    private static class SqlBuilderStatement {
        // mybatis的配置类
        private final Configuration configuration;
        // mybatis的脚本语言驱动
        private final LanguageDriver languageDriver;

        private SqlBuilderStatement(Configuration configuration) {
            this.configuration = configuration;
            this.languageDriver = configuration.getDefaultScriptingLanguageInstance();
        }

        private String newMsId(String sql, SqlCommandType sqlCommandType) {
            return sqlCommandType.toString() + "." + sql.hashCode();
        }

        private boolean hasMappedStatement(String msId) {
            return this.configuration.hasStatement(msId, false);
        }

        private void newSelectMappedStatement(String msId, SqlSource sqlSource, final Class<?> resultType) {
            MappedStatement ms = (new MappedStatement.Builder(this.configuration, msId, sqlSource, SqlCommandType.SELECT)).resultMaps(new ArrayList<ResultMap>() {
                {
                    this.add((new ResultMap.Builder(SqlBuilderStatement.this.configuration, "defaultResultMap", resultType, new ArrayList(0))).build());
                }
            }).build();
            this.configuration.addMappedStatement(ms);
        }

        private String select(String sql, Class<?> resultType) {
            String msId = this.newMsId(resultType + sql, SqlCommandType.SELECT);
            if (!this.hasMappedStatement(msId)) {
                StaticSqlSource sqlSource = new StaticSqlSource(this.configuration, sql);
                this.newSelectMappedStatement(msId, sqlSource, resultType);
            }
            return msId;
        }

        private String select(String sql) {
            String msId = this.newMsId(sql, SqlCommandType.SELECT);
            if (!this.hasMappedStatement(msId)) {
                StaticSqlSource sqlSource = new StaticSqlSource(this.configuration, sql);
                this.newSelectMappedStatement(msId, sqlSource, Map.class);
            }
            return msId;
        }

        private String selectDynamic(String sql, Class<?> parameterType) {
            String msId = this.newMsId(sql + parameterType, SqlCommandType.SELECT);
            if (!this.hasMappedStatement(msId)) {
                SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration, sql, parameterType);
                this.newSelectMappedStatement(msId, sqlSource, Map.class);
            }
            return msId;
        }

        private String selectDynamic(String sql, Class<?> parameterType, Class<?> resultType) {
            String msId = this.newMsId(resultType + sql + parameterType, SqlCommandType.SELECT);
            if (!this.hasMappedStatement(msId)) {
                SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration, sql, parameterType);
                this.newSelectMappedStatement(msId, sqlSource, resultType);
            }
            return msId;
        }
    }


}