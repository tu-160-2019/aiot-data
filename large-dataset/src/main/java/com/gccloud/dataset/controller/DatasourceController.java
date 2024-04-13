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

package com.gccloud.dataset.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisStopException;
import com.gccloud.common.permission.ApiPermission;
import com.gccloud.common.vo.PageVO;
import com.gccloud.common.vo.R;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dto.DatasetSearchDTO;
import com.gccloud.dataset.dto.DatasourceDTO;
import com.gccloud.dataset.dto.DatasourceSearchDTO;
import com.gccloud.dataset.dto.ExcelParseDTO;
import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.entity.DatasourceEntity;
import com.gccloud.dataset.entity.config.OriginalDataSetConfig;
import com.gccloud.dataset.service.IBaseDatasourceService;
import com.gccloud.dataset.service.factory.DatasourceServiceFactory;
import com.gccloud.dataset.service.impl.dataset.BaseDatasetServiceImpl;
import com.gccloud.dataset.service.impl.datasource.BaseDatasourceServiceImpl;
import com.gccloud.dataset.vo.DeleteCheckVO;
import com.gccloud.dataset.vo.ExcelParseVO;
import com.gccloud.dataset.vo.FieldInfoVO;
import com.gccloud.dataset.vo.TableInfoVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/2 11:32
 */
@Slf4j
@Api(tags = "数据源")
@RestController
@RequestMapping("/datasource")
public class DatasourceController {

    @Resource
    private BaseDatasourceServiceImpl baseDatasourceService;

    @Resource
    private DatasourceServiceFactory datasourceServiceFactory;

    @Resource
    private BaseDatasetServiceImpl datasetService;


    @ApiOperation("分页列表")
    @GetMapping("/page")
    @ApiPermission(permissions = {DatasetConstant.Permission.Datasource.VIEW})
    public R<PageVO<DatasourceEntity>> getPage(DatasourceSearchDTO searchDTO) {
        PageVO<DatasourceEntity> page = baseDatasourceService.getPage(searchDTO);
        return R.success(page);
    }


    @ApiOperation("列表")
    @GetMapping("/list")
    @ApiPermission(permissions = {DatasetConstant.Permission.Datasource.VIEW})
    public R<List<DatasourceEntity>> getList(DatasourceSearchDTO searchDTO) {
        List<DatasourceEntity> list = baseDatasourceService.getList(searchDTO);
        return R.success(list);
    }

    @ApiOperation("新增")
    @PostMapping("/add")
    @ApiPermission(permissions = {DatasetConstant.Permission.Datasource.ADD})
    public R<String> add(@RequestBody DatasourceDTO datasource) {
        IBaseDatasourceService datasourceService = datasourceServiceFactory.build(datasource.getSourceType());
        String id = datasourceService.add(datasource);
        return R.success(id);
    }

    @ApiOperation("修改")
    @PostMapping("/update")
    @ApiPermission(permissions = {DatasetConstant.Permission.Datasource.UPDATE})
    public R<Void> update(@RequestBody DatasourceDTO datasource) {
        IBaseDatasourceService datasourceService = datasourceServiceFactory.build(datasource.getSourceType());
        datasourceService.update(datasource);
        return R.success();
    }

    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    @ApiPermission(permissions = {DatasetConstant.Permission.Datasource.DELETE})
    public R<Void> delete(@PathVariable String id) {
        baseDatasourceService.delete(id);
        return R.success();
    }

    @ApiOperation("删除前检查")
    @PostMapping("/deleteCheck/{id}")
    @ApiPermission(permissions = {DatasetConstant.Permission.Datasource.DELETE})
    public R<DeleteCheckVO> deleteCheck(@PathVariable String id) {
        DeleteCheckVO deleteCheckVO = baseDatasourceService.deleteCheck(id);
        return R.success(deleteCheckVO);
    }

    @ApiOperation("测试连接")
    @PostMapping("/testConnect")
    @ApiPermission(permissions = {DatasetConstant.Permission.Datasource.TEST})
    public R<String> testConnect(@RequestBody DatasourceEntity datasourceEntity) {
        if (StringUtils.isBlank(datasourceEntity.getSourceType())) {
            return R.error("数据源类型不能为空");
        }
        IBaseDatasourceService datasourceService = datasourceServiceFactory.build(datasourceEntity.getSourceType());
        String info = datasourceService.sourceLinkTest(datasourceEntity);
        return R.success(info);
    }

    @ApiOperation("数据源名称重复判断")
    @PostMapping("/checkRepeat")
    @ApiPermission(permissions = {DatasetConstant.Permission.Datasource.VIEW})
    public R<Boolean> checkRepeat(@RequestBody DatasourceEntity datasource) {
        Boolean flag = baseDatasourceService.checkNameRepeat(datasource.getId(), datasource.getSourceName());
        return R.success(flag);
    }

    @ApiOperation("查询数据源下的表")
    @GetMapping("/getTableList/{sourceId}")
    @ApiPermission(permissions = {DatasetConstant.Permission.Datasource.VIEW})
    public R<List<TableInfoVO>> getTableList(@PathVariable String sourceId) {
        DatasourceEntity datasourceEntity = baseDatasourceService.getById(sourceId);
        if (datasourceEntity == null) {
            return R.error("数据源不存在");
        }
        IBaseDatasourceService datasourceService = datasourceServiceFactory.build(datasourceEntity.getSourceType());
        List<TableInfoVO> tableList = datasourceService.getTableList(datasourceEntity);
        DatasetSearchDTO searchDTO = new DatasetSearchDTO();
        searchDTO.setDatasetType(Lists.newArrayList(DatasetConstant.DataSetType.ORIGINAL));
        searchDTO.setSourceId(sourceId);
        List<DatasetEntity> originalList = datasetService.getList(searchDTO);
        List<String> tableNameList = originalList.stream().map((dataset) -> {
            OriginalDataSetConfig config = (OriginalDataSetConfig) dataset.getConfig();
            return config.getTableName();
        }).collect(Collectors.toList());
        tableList.forEach((table) -> {
            if (tableNameList.contains(table.getName())) {
                table.setStatus(1);
            }
        });
        return R.success(tableList);
    }


    @ApiOperation("查询数据源下的视图")
    @GetMapping("/getViewList/{sourceId}")
    @ApiPermission(permissions = {DatasetConstant.Permission.Datasource.VIEW})
    public R<List<TableInfoVO>> getViewList(@PathVariable String sourceId) {
        DatasourceEntity datasourceEntity = baseDatasourceService.getById(sourceId);
        if (datasourceEntity == null) {
            return R.error("数据源不存在");
        }
        IBaseDatasourceService datasourceService = datasourceServiceFactory.build(datasourceEntity.getSourceType());
        List<TableInfoVO> viewList = datasourceService.getViewList(datasourceEntity);
        DatasetSearchDTO searchDTO = new DatasetSearchDTO();
        searchDTO.setDatasetType(Lists.newArrayList(DatasetConstant.DataSetType.ORIGINAL));
        searchDTO.setSourceId(sourceId);
        List<DatasetEntity> originalList = datasetService.getList(searchDTO);
        List<String> tableNameList = originalList.stream().map((dataset) -> {
            OriginalDataSetConfig config = (OriginalDataSetConfig) dataset.getConfig();
            return config.getTableName();
        }).collect(Collectors.toList());
        viewList.forEach((view) -> {
            if (tableNameList.contains(view.getName())) {
                view.setStatus(1);
            }
        });
        return R.success(viewList);
    }


    @ApiOperation("查询数据源下表的字段信息")
    @GetMapping("/getFieldList/table/{sourceId}/{tableName}")
    @ApiPermission(permissions = {DatasetConstant.Permission.Datasource.VIEW})
    public R<List<FieldInfoVO>> getTableFieldList(@PathVariable String sourceId, @PathVariable String tableName) {
        DatasourceEntity datasourceEntity = baseDatasourceService.getById(sourceId);
        if (datasourceEntity == null) {
            return R.error("数据源不存在");
        }
        IBaseDatasourceService datasourceService = datasourceServiceFactory.build(datasourceEntity.getSourceType());
        List<FieldInfoVO> fieldList = datasourceService.getTableColumnList(datasourceEntity, tableName);
        return R.success(fieldList);
    }

    @ApiOperation("查询数据源下表的字段信息")
    @GetMapping("/getFieldList/view/{sourceId}/{tableName}")
    @ApiPermission(permissions = {DatasetConstant.Permission.Datasource.VIEW})
    public R<List<FieldInfoVO>> getViewFieldList(@PathVariable String sourceId, @PathVariable String tableName) {
        DatasourceEntity datasourceEntity = baseDatasourceService.getById(sourceId);
        if (datasourceEntity == null) {
            return R.error("数据源不存在");
        }
        IBaseDatasourceService datasourceService = datasourceServiceFactory.build(datasourceEntity.getSourceType());
        List<FieldInfoVO> fieldList = datasourceService.getViewColumnList(datasourceEntity, tableName);
        return R.success(fieldList);
    }


    @ApiOperation("Excel文件上传")
    @PostMapping("/uploadExcel")
    public R<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        // 将文件存储到 系统临时目录+固定文件件+随机文件名下
        // 保存文件后，删除文件夹内所有时间为一天前的文件
        File dir = new File(System.getProperty("java.io.tmpdir") + File.separator + "datasourceFile");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 删除文件夹内所有时间为一天前的文件
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.lastModified() < System.currentTimeMillis() - 24 * 60 * 60 * 1000) {
                    f.delete();
                }
            }
        }
        // 时间戳加随机字符串作为文件名
        String fileName = System.currentTimeMillis() + RandomStringUtils.randomAlphanumeric(6) + ".xlsx";
        String filePath = System.getProperty("java.io.tmpdir") + File.separator + "datasourceFile" + File.separator + fileName;
        // 保存文件
        try {
            file.transferTo(new File(filePath));
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return R.error("文件上传失败");
        }
        return R.success(fileName);
    }

    @ApiOperation("Excel文件解析")
    @PostMapping("/parseExcel")
    public R<ExcelParseVO> parseExcel(@RequestBody ExcelParseDTO parseDTO) {
        // 获取文件路径
        String filePath = System.getProperty("java.io.tmpdir") + File.separator + "datasourceFile" + File.separator + parseDTO.getFileName();
        // 获取文件
        File file = new File(filePath);
        if (!file.exists()) {
            return R.error("文件不存在");
        }
        List<Map<Integer, Object>> dataList = Lists.newArrayList();
        final Map<Integer, String>[] headInfo = new Map[]{Maps.newLinkedHashMap()};
        // 获取文件后缀 检查文件类型
        String suffix = FilenameUtils.getExtension(filePath);
        List<String> suffixList = Lists.newArrayList("xls", "xlsx");
        if (!suffixList.contains(suffix)) {
            return R.error("文件格式不正确");
        }
        // 根据表头行数，解析Excel文件，获取列信息
        EasyExcel.read(file, new AnalysisEventListener<Map<Integer, Object>>() {

            @Override
            public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                headInfo[0] = headMap;
            }

            @Override
            public void invoke(Map<Integer, Object> data, AnalysisContext context) {
                // 只取前10条数据
                if (dataList.size() > 10) {
                    throw new ExcelAnalysisStopException();
                }
                dataList.add(data);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {

            }

        }).headRowNumber(parseDTO.getHeadRowNum()).sheet().doRead();
        // 组装预览数据
        List<Map<String, Object>> previewList = Lists.newArrayList();
        for (Map<Integer, Object> dataMap : dataList) {
            // 将索引转换为表头名称
            Map<String, Object> previewMap = Maps.newLinkedHashMap();
            for (Integer index : dataMap.keySet()) {
                String headName = headInfo[0].get(index);
                Object value = dataMap.get(index);
                previewMap.put(headName, value);
                previewList.add(previewMap);
            }
        }
        // 组装表头配置
        List<Map<String, Object>> headInfoList = Lists.newArrayList();
        for (Integer index : headInfo[0].keySet()) {
            Map<String, Object> headerConfig = Maps.newLinkedHashMap();
            headerConfig.put("index", index);
            headerConfig.put("title", headInfo[0].get(index));
            headerConfig.put("field", "column" + index);
            headInfoList.add(headerConfig);
        }
        ExcelParseVO parseVO = new ExcelParseVO();
        parseVO.setHeadMap(headInfoList);
        parseVO.setDataList(previewList);
        // 解析Excel文件，返回表头信息和预览数据
        return R.success(parseVO);
    }

}