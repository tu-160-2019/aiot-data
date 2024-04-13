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

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Cell;
import com.alibaba.excel.metadata.CellData;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gccloud.common.exception.GlobalException;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dao.DatasourceDao;
import com.gccloud.dataset.dto.DatasourceDTO;
import com.gccloud.dataset.dto.ExcelHeaderDTO;
import com.gccloud.dataset.entity.DatasourceEntity;
import com.gccloud.dataset.service.IBaseDatasourceService;
import com.gccloud.dataset.vo.DataVO;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * excel数据源，上传excel文件，解析excel文件，将excel文件解析成表格数据，存储到数据库中，然后通过sql查询数据
 *
 * @author hongyang
 * @version 1.0
 * @date 2023/6/2 11:06
 */
@Service(DatasetConstant.DatasourceType.EXCEL)
public class ExcelDatasourceServiceImpl extends ServiceImpl<DatasourceDao, DatasourceEntity> implements IBaseDatasourceService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 每隔N条存储数据库，实际使用中可以1000条或者更多，然后清理cachedDataList ，方便内存回收
     * 如果表格数据量很大,成千上万条,可以分批进行入库,数量不多的话,没必要做分配
     */
    private static final int BATCH_COUNT = 100;

    /**
     * 缓存的数据
     */
    private List<Map<Integer, Object>> cachedDataList = new ArrayList<>(BATCH_COUNT);


    @Override
    public String add(DatasourceDTO datasourceDTO) {
        Map<String, Object> config = datasourceDTO.getConfig();
        String fileName = config.get("fileName").toString();
        String filePath = System.getProperty("java.io.tmpdir") + File.separator + "datasourceFile" + File.separator + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            throw new GlobalException("文件已过期，请重新上传");
        }
        // 新增时需要1、根据前端的传的配置生成数据库的表 2、获取excel文件，解析成表格数据，存储到建好的数据库表中
        // 根据数据集id生成表名,加上随机字母
        String tableName = "excel_" + RandomStringUtils.randomAlphabetic(5);
        datasourceDTO.setTableName(tableName);
        // 类型转换可能会出错，应该需要前端添加className
        List<ExcelHeaderDTO> headerList = (List<ExcelHeaderDTO>) config.get("headerList");
        StringBuilder ddl = new StringBuilder("create table " + tableName + " (");
        ddl.append("`id` bigint(64) NOT NULL AUTO_INCREMENT COMMENT '主键',");
        for (ExcelHeaderDTO header : headerList) {
            String name = header.getColumnName();
            String type = header.getColumnType();
            ddl.append("`" + name + "` " + this.getDataType(type) + " DEFAULT NULL COMMENT '" + name + "',");
        }
        ddl.append("PRIMARY KEY (`id`)");
        ddl.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='excel数据源表");
        ddl.append(datasourceDTO.getSourceName()).append("';");
        try {
            jdbcTemplate.execute(ddl.toString());
        } catch (Exception e) {
            throw new GlobalException("创建表失败");
        }
        Map<Integer, ExcelHeaderDTO> headerMap = headerList.stream().collect(Collectors.toMap(ExcelHeaderDTO::getIndex, Function.identity()));
        Integer headRowNum = Integer.valueOf(config.get("headRowNum").toString());
        // 解析excel文件，获取数据
        EasyExcel.read(file, new AnalysisEventListener<Map<Integer, Object>>() {

            @Override
            public void invoke(Map<Integer, Object> data, AnalysisContext context) {
                cachedDataList.add(data);
                if (cachedDataList.size() >= BATCH_COUNT) {
                    saveData();
                    cachedDataList.clear();
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {

            }

            /**
             * 保存数据到数据库
             */
            private void saveData() {
                StringBuilder sql = new StringBuilder("insert into " + tableName + " (");
                // 将headerMap的key按照升序取出columnName放到columnList中
                List<String> columnList = headerMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(entry -> entry.getValue().getColumnName()).collect(Collectors.toList());
                // 按照索引升序拼接sql的列名
                columnList.forEach(column -> sql.append(column).append(","));
                sql.deleteCharAt(sql.length() - 1);
                sql.append(") values ");
                // 拼接values
                for (Map<Integer, Object> data : cachedDataList) {
                    sql.append("(");
                    for (int i = 0; i < data.size(); i++) {
                        // TODO headerMap中的key是从0开始的吗
                        ExcelHeaderDTO headerDTO = headerMap.get(i);
                        String type = headerDTO.getColumnType();
                        String value = data.get(i).toString();
                        if ("number".equalsIgnoreCase(type)) {
                            sql.append(value).append(",");
                            continue;
                        }
                        sql.append("'").append(value).append("',");
                    }
                    sql.deleteCharAt(sql.length() - 1);
                    sql.append("),");
                }
                sql.deleteCharAt(sql.length() - 1);
                jdbcTemplate.execute(sql.toString());
            }

        }).headRowNumber(headRowNum).sheet().doRead();

        return IBaseDatasourceService.super.add(datasourceDTO);
    }


    /**
     * 根据数据类型获取数据库的数据类型
     *
     * @param type
     * @return
     */
    private String getDataType(String type) {
        if (StringUtils.isBlank(type)) {
            return "text";
        }
        switch (type.toLowerCase()) {
            case "string":
                return "text";
            case "number":
                return "bigint(64)";
            case "date":
                return "date";
            case "datetime":
                return "datetime";
            default:
                return "text";
        }
    }


    /**
     * 解析excel文件，获取表格每列的数据类型
     *
     * @param filePath
     * @param headerNum
     * @return key:列号，value:数据类型
     */
    private Map<Integer, String> parseExcelDataType(String filePath, Integer headerNum) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new GlobalException("文件已过期，请重新上传");
        }
        // 第一遍解析excel文件，获取表格每列的数据类型，这次解析不获取数据，只获取表头和数据类型
        EasyExcel.read(file, new AnalysisEventListener<Map<Integer, Object>>() {

            @Override
            public void invoke(Map<Integer, Object> data, AnalysisContext context) {
                Map<Integer, Cell> cellMap = context.readRowHolder().getCellMap();
                cellMap.forEach((k, v) -> {
                    CellData cellData = (CellData) v;
                    System.out.println(cellData.getType());
                });


            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {

            }


        }).headRowNumber(headerNum).sheet().doRead();


        return null;
    }


    @Override
    public void update(DatasourceDTO entity) {
        // excel数据集不允许编辑
        IBaseDatasourceService.super.update(entity);
    }

    @Override
    public void delete(String id) {
        DatasourceEntity datasource = this.getById(id);
        IBaseDatasourceService.super.delete(id);
        // 删除数据集还需要额外删除数据库的表
        String tableName = datasource.getTableName();
        try {
            jdbcTemplate.execute("drop table if exists " + tableName);
        } catch (Exception e) {
            throw new GlobalException("删除表失败");
        }
    }

    @Override
    public DataVO executeSql(DatasourceEntity datasource, String sql) {
        // 和mysql数据源的实现一样
        return null;
    }

    @Override
    public DataVO executeSqlPage(DatasourceEntity datasource, String sql, Integer current, Integer size) {
        // 和mysql的实现一样
        return IBaseDatasourceService.super.executeSqlPage(datasource, sql, current, size);
    }


}