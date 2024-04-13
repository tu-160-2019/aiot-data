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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gccloud.common.permission.ApiPermission;
import com.gccloud.common.utils.BeanConvertUtils;
import com.gccloud.common.utils.JSON;
import com.gccloud.common.vo.PageVO;
import com.gccloud.common.vo.R;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dto.*;
import com.gccloud.dataset.entity.CategoryEntity;
import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.entity.DatasourceEntity;
import com.gccloud.dataset.entity.LabelEntity;
import com.gccloud.dataset.service.IBaseDataSetService;
import com.gccloud.dataset.service.ICategoryService;
import com.gccloud.dataset.service.IDatasetLabelService;
import com.gccloud.dataset.service.factory.DataSetServiceFactory;
import com.gccloud.dataset.service.impl.dataset.BaseDatasetServiceImpl;
import com.gccloud.dataset.service.impl.datasource.BaseDatasourceServiceImpl;
import com.gccloud.dataset.utils.DBUtils;
import com.gccloud.dataset.utils.MybatisParameterUtils;
import com.gccloud.dataset.vo.DataVO;
import com.gccloud.dataset.vo.DatasetInfoVO;
import com.gccloud.dataset.vo.DatasetVO;
import com.gccloud.dataset.vo.DeleteCheckVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 11:47
 */
@Api(tags = "数据集")
@RestController
@RequestMapping("/dataset")
public class DatasetController {

    @Resource
    private BaseDatasetServiceImpl baseDatasetService;

    @Resource
    private DataSetServiceFactory dataSetServiceFactory;

    @Resource
    private BaseDatasourceServiceImpl datasourceService;

    @Resource
    private IDatasetLabelService datasetLabelService;

    @Resource
    private ICategoryService categoryService;

    @Resource
    private MybatisParameterUtils parameterUtils;


    @ApiOperation("分页列表")
    @GetMapping("/page")
    @ApiPermission(permissions = {DatasetConstant.Permission.Dataset.VIEW})
    public R<PageVO<DatasetVO>> getPage(DatasetSearchDTO searchDTO) {
        List<String> labelIds = searchDTO.getLabelIds();
        List<String> datasetIdList = this.filterDatasetByIdList(labelIds);
        searchDTO.setDatasetIds(datasetIdList);
        if (CollectionUtils.isNotEmpty(labelIds) && CollectionUtils.isEmpty(datasetIdList)) {
            return R.success(new PageVO<>());
        }
        PageVO<DatasetEntity> page = baseDatasetService.getPage(searchDTO);
        PageVO<DatasetVO> pageVO = BeanConvertUtils.convert(page, PageVO.class);
        List<DatasetVO> voList = BeanConvertUtils.convert(page.getList(), DatasetVO.class);
        for (DatasetVO datasetVO : voList) {
            List<String> labelIdList = datasetLabelService.getLabelIdsByDatasetId(datasetVO.getId());
            datasetVO.setLabelIds(labelIdList);
        }
        pageVO.setList(voList);
        return R.success(pageVO);
    }

    @ApiOperation("列表")
    @GetMapping("/list")
    @ApiPermission(permissions = {DatasetConstant.Permission.Dataset.VIEW})
    public R<List<DatasetEntity>> getList(DatasetSearchDTO searchDTO) {
        List<String> labelIds = searchDTO.getLabelIds();
        List<String> datasetIdList = this.filterDatasetByIdList(labelIds);
        searchDTO.setDatasetIds(datasetIdList);
        if (CollectionUtils.isNotEmpty(labelIds) && CollectionUtils.isEmpty(datasetIdList)) {
            return R.success(Lists.newArrayList());
        }
        List<DatasetEntity> list = baseDatasetService.getList(searchDTO);
        return R.success(list);
    }

    /**
     * 根据标签id列表过滤数据集
     *
     * @param labelIds
     * @return
     */
    private List<String> filterDatasetByIdList(List<String> labelIds) {
        if (labelIds == null || labelIds.isEmpty()) {
            return null;
        }
        return datasetLabelService.getDatasetIdsByLabelIds(labelIds, true);
    }


    @ApiOperation("新增")
    @PostMapping("/add")
    @ApiPermission(permissions = {DatasetConstant.Permission.Dataset.ADD})
    public R<String> add(@RequestBody DatasetDTO datasetDTO) {
        IBaseDataSetService dataSetService = dataSetServiceFactory.build(datasetDTO.getDatasetType());
        String id = dataSetService.add(datasetDTO);
        // 保存与标签的关联关系
        List<String> labelIds = datasetDTO.getLabelIds();
        if (labelIds == null || labelIds.isEmpty()) {
            return R.success(id);
        }
        datasetLabelService.addByDatasetId(id, labelIds);
        return R.success(id);
    }

    @ApiOperation("修改")
    @PostMapping("/update")
    @ApiPermission(permissions = {DatasetConstant.Permission.Dataset.UPDATE})
    public R<Void> update(@RequestBody DatasetDTO datasetDTO) {
        IBaseDataSetService dataSetService = dataSetServiceFactory.build(datasetDTO.getDatasetType());
        dataSetService.update(datasetDTO);
        // 更新与标签的关联关系
        List<String> labelIds = datasetDTO.getLabelIds();
        datasetLabelService.deleteByDatasetId(datasetDTO.getId());
        if (labelIds == null || labelIds.isEmpty()) {
            return R.success();
        }
        datasetLabelService.addByDatasetId(datasetDTO.getId(), labelIds);
        return R.success();
    }

    @ApiOperation("删除")
    @PostMapping("/delete/{id}")
    @ApiPermission(permissions = {DatasetConstant.Permission.Dataset.DELETE})
    public R<Void> delete(@PathVariable("id") String id) {
        IBaseDataSetService dataSetService = dataSetServiceFactory.buildById(id);
        dataSetService.delete(id);
        datasetLabelService.deleteByDatasetId(id);
        return R.success();
    }

    @ApiOperation("删除前检查")
    @PostMapping("/deleteCheck/{id}")
    @ApiPermission(permissions = {DatasetConstant.Permission.Dataset.DELETE})
    public R<DeleteCheckVO> deleteCheck(@PathVariable("id") String id) {
        // 直接使用基础数据集服务，后续看情况是否需要改成工厂模式
        DeleteCheckVO deleteCheckVO = baseDatasetService.deleteCheck(id);
        return R.success(deleteCheckVO);
    }


    @ApiOperation("检查数据集分组下有多少数据集")
    @GetMapping("/getCountByType/{typeId}")
    @ApiPermission(permissions = {DatasetConstant.Permission.Dataset.VIEW})
    public R<Integer> getCountByType(@PathVariable("typeId") String typeId) {
        CategoryEntity category = categoryService.getById(typeId);
        if (category == null) {
            return R.success(0);
        }
        String ids = category.getIds() + ",";
        LambdaQueryWrapper<CategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(CategoryEntity::getId);
        queryWrapper.likeRight(CategoryEntity::getIds, ids);
        List<CategoryEntity> list = categoryService.list(queryWrapper);
        List<String> typeIds = list.stream().map(CategoryEntity::getId).collect(Collectors.toList());
        typeIds.add(typeId);
        LambdaQueryWrapper<DatasetEntity> datasetQueryWrapper = new LambdaQueryWrapper<>();
        datasetQueryWrapper.select(DatasetEntity::getId);
        datasetQueryWrapper.in(DatasetEntity::getTypeId, typeIds);
        List<DatasetEntity> sizeList = baseDatasetService.list(datasetQueryWrapper);
        return R.success(sizeList.size());
    }


    @ApiOperation("详情")
    @GetMapping("/info/{id}")
    @ApiPermission(permissions = {DatasetConstant.Permission.Dataset.VIEW})
    public R<DatasetEntity> info(@PathVariable("id") String id) {
        IBaseDataSetService dataSetService = dataSetServiceFactory.buildById(id);
        DatasetEntity datasetEntity = dataSetService.getById(id);
        return R.success(datasetEntity);
    }

    @ApiOperation("数据集详情")
    @GetMapping("/datasetInfo/{id}")
    @ApiPermission(permissions = {DatasetConstant.Permission.Dataset.VIEW})
    public R<DatasetInfoVO> getDatasetInfo(@PathVariable("id") String id) {
        IBaseDataSetService dataSetService = dataSetServiceFactory.buildById(id);
        DatasetInfoVO infoVO = dataSetService.getInfoById(id);
        List<LabelEntity> labels = datasetLabelService.getLabelByDatasetId(id);
        infoVO.setLabelList(labels);
        return R.success(infoVO);
    }

    @ApiOperation("数据集名称重复判断")
    @PostMapping("/checkRepeat")
    @ApiPermission(permissions = {DatasetConstant.Permission.Dataset.VIEW})
    public R<Boolean> checkRepeat(@RequestBody DatasetEntity datasetEntity) {
        boolean nameRepeat = baseDatasetService.checkNameRepeat(datasetEntity.getId(), datasetEntity.getName());
        return R.success(nameRepeat);
    }

    @ApiOperation("数据集执行测试")
    @PostMapping("/execute/test")
    @ApiPermission(permissions = {DatasetConstant.Permission.Dataset.EXECUTE})
    public R<Object> execute(@RequestBody TestExecuteDTO executeDTO) {
        if (StringUtils.isBlank(executeDTO.getDataSetType())) {
            return R.error("数据集类型不能为空");
        }
        List<DatasetParamDTO> params = executeDTO.getParams();
        if (params != null && !params.isEmpty()) {
            for (DatasetParamDTO param : params) {
                // 检查testValue，如果不是空，则覆盖value
                if (StringUtils.isNotBlank(param.getTestValue())) {
                    param.setValue(param.getTestValue());
                }
            }
        }
        // 获取对应的数据集服务
        IBaseDataSetService dataSetService = dataSetServiceFactory.build(executeDTO.getDataSetType());
        DataVO execute = dataSetService.execute(executeDTO);
        Map<String, Object> result = Maps.newHashMap();
        result.put("data", execute.getData());
        result.put("structure", execute.getStructure());
        if (StringUtils.isNotBlank(executeDTO.getDataSourceId())) {
            DatasourceEntity datasource = datasourceService.getInfoById(executeDTO.getDataSourceId());
            if (executeDTO.getDataSetType().equals(DatasetConstant.DataSetType.ORIGINAL)) {
                String originalString = executeDTO.getScript();
                JSONObject originalTest = JSON.parseObject(originalString);
                String tableName = originalTest.getString("tableName");
                executeDTO.setScript("select 1 from " + tableName);
            }
            // 构造sql
            String sql;
            if (DatasetConstant.SyntaxType.MYBATIS.equals(executeDTO.getSyntaxType())) {
                // 使用mybatis语法规则进行sql构造
                sql = parameterUtils.updateParamsConfig(executeDTO.getScript(), executeDTO.getParams());
            } else {
                sql = DBUtils.updateParamsConfig(executeDTO.getScript(), executeDTO.getParams());
            }
            List<String> tableNameList = DBUtils.getTableNames(sql, datasource.getSourceType());
            result.put("tableNameList", tableNameList);
        }
        return R.success(result);
    }


    @ApiOperation("数据集执行")
    @PostMapping("/execute")
    @ApiPermission(permissions = {DatasetConstant.Permission.Dataset.EXECUTE})
    public R<Object> execute(@RequestBody ExecuteDTO executeDTO) {
        if (StringUtils.isBlank(executeDTO.getDataSetType()) && StringUtils.isBlank(executeDTO.getDataSetId())) {
            return R.error("数据集id不能为空");
        }
        // 获取对应的数据集服务
        IBaseDataSetService dataSetService;
        if (StringUtils.isBlank(executeDTO.getDataSetType())) {
            dataSetService = dataSetServiceFactory.buildById(executeDTO.getDataSetId());
        } else {
            dataSetService = dataSetServiceFactory.build(executeDTO.getDataSetType());
        }
        boolean executionNeeded = dataSetService.checkBackendExecutionNeeded(executeDTO.getDataSetId());
        Object data;
        if (executeDTO.getCurrent() != null && executeDTO.getSize() != null) {
            data = dataSetService.execute(executeDTO.getDataSetId(), executeDTO.getParams(), executeDTO.getCurrent(), executeDTO.getSize());
        } else {
            data = dataSetService.execute(executeDTO.getDataSetId(), executeDTO.getParams());
        }
        Map<String, Object> result = Maps.newHashMap();
        result.put("data", data);
        // 是否需要由前端执行
        result.put("executionByFrontend", !executionNeeded);
        return R.success(result);
    }


}