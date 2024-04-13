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
import com.gccloud.common.utils.BeanConvertUtils;
import com.gccloud.common.utils.GroovyUtils;
import com.gccloud.common.utils.HttpUtils;
import com.gccloud.common.utils.JSON;
import com.gccloud.dataset.constant.DatasetConstant;
import com.gccloud.dataset.dao.DatasetDao;
import com.gccloud.dataset.dto.DatasetParamDTO;
import com.gccloud.dataset.dto.TestExecuteDTO;
import com.gccloud.dataset.entity.DatasetEntity;
import com.gccloud.dataset.entity.config.HttpDataSetConfig;
import com.gccloud.dataset.params.ParamsClient;
import com.gccloud.dataset.permission.DatasetPermissionClient;
import com.gccloud.dataset.service.IBaseDataSetService;
import com.gccloud.dataset.vo.DataVO;
import com.gccloud.dataset.vo.DatasetInfoVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author hongyang
 * @version 1.0
 * @date 2023/6/1 11:20
 */
@Slf4j
@Service(DatasetConstant.DataSetType.HTTP)
public class HttpDataSetServiceImpl extends ServiceImpl<DatasetDao, DatasetEntity> implements IBaseDataSetService {

    @Resource
    private ParamsClient paramsClient;

    @Resource
    private DatasetPermissionClient datasetPermissionClient;

    /**
     * 前端执行
     */
    public static final String FRONTEND = "frontend";
    /**
     * 后端执行
     */
    public static final String BACKEND = "backend";


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
    public Object execute(String id, List<DatasetParamDTO> paramList) {
        if (StringUtils.isBlank(id)) {
            throw new GlobalException("数据集id不能为空");
        }
        DatasetEntity entity = this.getByIdFromCache(id);
        if (entity == null) {
            throw new GlobalException("数据集不存在");
        }
        final List<DatasetParamDTO> finalParamList = Lists.newArrayList(paramList);
        if (DatasetConstant.DatasetCache.OPEN.equals(entity.getCache())) {
            CompletableFuture<Object> future = DATASET_CACHE.get(id, key -> getData(entity, finalParamList));
            try {
                return future.get();
            } catch (Exception e) {
                log.error("数据集缓存异常：{}", e.getMessage());
                log.error(ExceptionUtils.getStackTrace(e));
            }

        }
        return getData(entity, finalParamList);
    }

    /**
     * 获取数据
     *
     * @param entity
     * @param finalParamList
     * @return
     */
    private Object getData(DatasetEntity entity, List<DatasetParamDTO> finalParamList) {
        HttpDataSetConfig config = (HttpDataSetConfig) entity.getConfig();
        // NOTE 复制一份config，避免直接修改缓存
        // NOTE 2,  BeanConvertUtils的复制方式不可靠，源对象内部的对象引用会被复制，导致修改复制对象的值，源对象也会被修改
        HttpDataSetConfig configCopy = JSON.parseObject(JSON.toJSONString(config), HttpDataSetConfig.class);
        // 自定义参数拓展处理
        List<DatasetParamDTO> params = paramsClient.handleParams(finalParamList);
        // http请求参数部分的处理
        configCopy = this.handleParams(configCopy, params);
        if (configCopy.getRequestType().equals(FRONTEND)) {
            log.info("执行【{}】数据集（类型：【HTTP】，ID:【{}】）， 方式：【前端代理】， 类型：【{}】， URL：【{}】", entity.getName(), entity.getId(), configCopy.getMethod(), configCopy.getUrl());
            // 将params替换掉config中的值
            if (params != null && !params.isEmpty()) {
                List<DatasetParamDTO> configParams = configCopy.getParamsList();
                for (DatasetParamDTO param : params) {
                    // 如果有name相同的，替换掉
                    for (DatasetParamDTO configParam : configParams) {
                        if (param.getName().equals(configParam.getName())) {
                            configParam.setValue(param.getValue());
                            break;
                        }
                    }
                }
            }
            return configCopy;
        }
        return this.getBackendData(configCopy, entity);
    }


    @Override
    public DataVO execute(TestExecuteDTO executeDTO) {
        String apiInfoJson = executeDTO.getScript();
        if (StringUtils.isBlank(apiInfoJson)) {
            throw new GlobalException("数据集测试数据不能为空");
        }
        apiInfoJson = paramsClient.handleScript(executeDTO.getDataSetType(), apiInfoJson);
        HttpDataSetConfig config = JSON.parseObject(apiInfoJson, HttpDataSetConfig.class);
        List<DatasetParamDTO> paramList = executeDTO.getParams();
        paramList = paramsClient.handleParams(paramList);

        config = this.handleParams(config, paramList);
        DataVO dataVO = new DataVO();
        if (config.getRequestType().equals(FRONTEND)) {
            dataVO.setData(config);
            return dataVO;
        }
        Object data = this.getBackendData(config, null);
        dataVO.setData(data);
        return dataVO;
    }

    /**
     * 由前端执行请求，这里只需替换参数信息
     *
     * @param config
     * @param datasetParamList
     * @return
     */
    private HttpDataSetConfig handleParams(HttpDataSetConfig config, List<DatasetParamDTO> datasetParamList) {
        if (datasetParamList == null || datasetParamList.size() == 0) {
            return config;
        }
        // 处理header中的参数
        List<Map<String, Object>> headers = config.getHeaders();
        if (headers != null && headers.size() > 0) {
            for (Map<String, Object> header : headers) {
                String value = (String) header.get("value");
                if (StringUtils.isBlank(value)) {
                    continue;
                }
                // 检查是否包含${}格式的参数
                if (!value.contains("${")) {
                    continue;
                }
                for (DatasetParamDTO param : datasetParamList) {
                    if (value.contains("${" + param.getName() + "}")) {
                        String replaceValue = this.parameterReplace(param, (String) header.get("value"));
                        header.put("value", replaceValue);
                    }
                }
            }
        }
        // 处理params中的参数
        List<Map<String, Object>> httpParams = config.getParams();
        if (httpParams != null && httpParams.size() > 0) {
            for (Map<String, Object> param : httpParams) {
                String value = (String) param.get("value");
                if (StringUtils.isBlank(value)) {
                    continue;
                }
                // 检查是否包含${}格式的参数
                if (!value.contains("${")) {
                    continue;
                }
                for (DatasetParamDTO paramDTO : datasetParamList) {
                    if (value.contains("${" + paramDTO.getName() + "}")) {
                        String replaceValue = this.parameterReplace(paramDTO, (String) param.get("value"));
                        param.put("value", replaceValue);
                    }
                }
            }
        }
        String body = config.getBody();
        if (StringUtils.isNotBlank(body)) {
            for (DatasetParamDTO param : datasetParamList) {
                if (body.contains("${" + param.getName() + "}")) {
                    body = this.parameterReplace(param, body);
                }
            }
            config.setBody(body);
        }
        // 处理url中的参数
        String url = config.getUrl();
        if (StringUtils.isNotBlank(url)) {
            for (DatasetParamDTO param : datasetParamList) {
                if (url.contains(param.getName())) {
                    url = this.parameterReplace(param, url);
                }
            }
            config.setUrl(url);
        }
        // 处理请求脚本中的参数
        String requestScript = config.getRequestScript();
        if (StringUtils.isNotBlank(requestScript)) {
            for (DatasetParamDTO param : datasetParamList) {
                if (requestScript.contains(param.getName())) {
                    requestScript = this.parameterReplace(param, requestScript);
                }
            }
            config.setRequestScript(requestScript);
        }
        // 处理响应脚本中的参数
        String responseScript = config.getResponseScript();
        if (StringUtils.isNotBlank(responseScript)) {
            for (DatasetParamDTO param : datasetParamList) {
                if (responseScript.contains(param.getName())) {
                    responseScript = this.parameterReplace(param, responseScript);
                }
            }
            config.setResponseScript(responseScript);
        }
        return config;

    }

    private Object getBackendData(HttpDataSetConfig config, DatasetEntity entity) {
        long startTime = System.currentTimeMillis();
        // 请求头
        Map<String, String> headers = config.getHeaders() == null ? Maps.newHashMap() : config.getHeaders().stream().collect(Collectors.toMap(item -> (String) item.get("key"), item -> (String) item.get("value")));
        // 请求参数
        Map<String, Object> params = Maps.newHashMap();
        if (config.getParams() != null) {
            for (Map<String, Object> param : config.getParams()) {
                String key = (String) param.get("key");
                // 如果有多个同名参数，使用List存储所有参数值
                if (!params.containsKey(key)) {
                    params.put(key, param.get("value"));
                    continue;
                }
                Object valueObj = params.get(key);
                if (valueObj instanceof List) {
                    ((List) valueObj).add(param.get("value"));
                } else {
                    List<Object> valueList = Lists.newArrayList();
                    valueList.add(valueObj);
                    valueList.add(param.get("value"));
                    params.put(key, valueList);
                }
            }
        }
        String body = config.getBody();
        // 如果有请求前脚本，则执行请求前脚本
        Map<String, Object> reqParams = Maps.newHashMap();
        // url参数，默认为空，如果在脚本中有赋值，则后续拼接url时会使用这里的赋值（参数优先级 脚本赋值>参数预处理>外部入参>默认值）
        reqParams.put("url", Maps.newHashMap());
        reqParams.put("headers", headers);
        reqParams.put("params", params);
        reqParams.put("data", body);
        if (StringUtils.isNotBlank(config.getRequestScript())) {
            Map<String, Object> requestScriptMap = Maps.newHashMap();
            requestScriptMap.put("req", reqParams);
            GroovyUtils.run(config.getRequestScript(), requestScriptMap);
        }
        // 替换url中的参数
        Map<String, Object> paramsInUrl = getUrlParams(config.getUrl());
        if (!paramsInUrl.isEmpty()) {
            // 去除url中的参数，后面整体重新拼接
            config.setUrl(config.getUrl().replaceAll("\\?.*", ""));
            Map<String, Object> urlParams = (Map<String, Object>) reqParams.get("url");
            urlParams = urlParams == null ? Maps.newHashMap() : urlParams;
            // 遍历url参数，如果url参数在脚本中有赋值，则使用脚本中的赋值
            for (Map.Entry<String, Object> entry : paramsInUrl.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (!urlParams.containsKey(key)) {
                    // 将原本的url参数放入params中，一会儿重新拼接
                    if (params.containsKey(key)) {
                        Object valueObj = params.get(key);
                        // 检查value 和 valueObj的类型，如果都是List，则合并；如果都不是List，则转换成List合并；如果一个是List，一个不是，则将不是的添加到List中
                        if (valueObj instanceof List) {
                            if (value instanceof List) {
                                ((List) valueObj).addAll((List) value);
                            } else {
                                ((List) valueObj).add(value);
                            }
                            params.put(key, valueObj);
                        } else {
                            List<Object> valueList = Lists.newArrayList();
                            valueList.add(valueObj);
                            if (value instanceof List) {
                                valueList.addAll((List) value);
                            } else {
                                valueList.add(value);
                            }
                            params.put(key, valueList);
                        }
                    } else {
                        params.put(key, value);
                    }
                    continue;
                }
                // 如果url中的参数在脚本中有赋值，则直接使用脚本中的赋值
                params.put(key, urlParams.get(key));
            }
        }
        // 拼接url和参数
        StringBuilder url = new StringBuilder(config.getUrl());
        if (params.size() > 0) {
            if (url.indexOf("?") == -1) {
                url.append("?");
            } else {
                url.append("&");
            }
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                String key = entry.getKey();
                if (entry.getValue() instanceof List) {
                    List<Object> valueList = (List<Object>) entry.getValue();
                    for (Object value : valueList) {
                        url.append(key).append("=").append(value).append("&");
                    }
                    continue;
                }
                String value = String.valueOf(entry.getValue());
                url.append(key).append("=").append(value).append("&");
            }
            url = new StringBuilder(url.substring(0, url.length() - 1));
        }
        if (entity == null) {
            log.info("测试数据集（类型：【HTTP】）， 方式：【后端代理】， 类型：【{}】， URL：【{}】， header：【{}】， params：【{}】， body：【{}】",
                    config.getMethod(), url, JSON.toJSONString(headers), JSON.toJSONString(params), body);
        } else {
            log.info("执行【{}】数据集（类型：【HTTP】，ID:【{}】）， 方式：【后端代理】， 类型：【{}】， URL：【{}】， header：【{}】， params：【{}】， body：【{}】",
                    entity.getName(), entity.getId(), config.getMethod(), url, JSON.toJSONString(headers), JSON.toJSONString(params), body);
        }
        // 发送请求
        Response response = null;
        switch (config.getMethod().toUpperCase()) {
            case "GET":
                response = HttpUtils.get(url.toString(), headers);
                break;
            case "POST":
                Map<String, Object> upperCaseHeaders = Maps.newHashMap();
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    upperCaseHeaders.put(entry.getKey().toUpperCase(), entry.getValue());
                }
                // 从Header中取Content-Type
                Object contentTypeObj = upperCaseHeaders.get("Content-Type".toUpperCase());
                String contentType = contentTypeObj == null ? "" : contentTypeObj.toString();
                if (contentTypeObj == null && (reqParams.get("data") == null || reqParams.get("data").toString().length() == 0)) {
                    // HttpUtils.post传入的contentType为空时，则默认为application/json，这时如果data为空，需要将data置为空json{}
                    body = "{}";
                } else {
                    body = reqParams.get("data") == null ? "" : reqParams.get("data").toString();
                }
                response = HttpUtils.post(url.toString(), contentType, headers, body);
                break;
            default:
                throw new GlobalException("不支持的请求方式");
        }
        if (!response.isSuccessful()) {
            String message = response.message();
            String errorMessage = "请求失败 code: " + response.code();
            if (StringUtils.isNotBlank(message)) {
                errorMessage += " message: " + message;
            }
            throw new GlobalException(errorMessage);
        }
        String responseBody = null;
        try {
            responseBody = response.body().string();
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new GlobalException("获取请求响应失败");
        }
        Object returnResult = responseBody;
        // 如果有响应后脚本，则执行响应后脚本
        boolean runResponseScript = StringUtils.isNotBlank(config.getResponseScript());
        if (runResponseScript) {
            Map<String, Object> responseScriptMap = Maps.newHashMap();
            // 取name和value
            responseScriptMap.put("responseString", responseBody);
            returnResult = GroovyUtils.run(config.getResponseScript(), responseScriptMap);
        } else {
            if (responseBody.startsWith("{")) {
                returnResult = JSON.parseObject(responseBody);
            }
            if (responseBody.startsWith("[")) {
                returnResult = JSON.parseArray(responseBody);
            }
        }
        long endTime = System.currentTimeMillis();
        if (entity == null) {
            log.info("测试数据集（类型：【HTTP】）结束， 耗时：【{}】ms", endTime - startTime);
        } else {
            log.info("执行【{}】数据集（类型：【HTTP】，ID:【{}】）结束, 耗时：【{}】ms", entity.getName(), entity.getId(), endTime - startTime);
        }
        return returnResult;
    }

    /**
     * 替换 ${} 格式的参数
     *
     * @param param
     * @param str
     * @return
     */
    private String parameterReplace(DatasetParamDTO param, String str) {
        str = str.replaceAll("\\$\\{" + param.getName() + "\\}", param.getValue());
        return str;
    }

    /**
     * 从url中解析出参数
     *
     * @param urlString
     * @return
     */
    private Map<String, Object> getUrlParams(String urlString) {
        Map<String, Object> map = Maps.newHashMap();
        try {
            URL url = new URL(urlString);
            String query = url.getQuery();
            String[] params = query.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length != 2) {
                    continue;
                }
                String key = URLDecoder.decode(keyValue[0], "UTF-8");
                String value = URLDecoder.decode(keyValue[1], "UTF-8");
                if (!map.containsKey(key)) {
                    map.put(key, value);
                    continue;
                }
                Object valueObj = map.get(key);
                if (valueObj instanceof List) {
                    List<String> values = (List<String>) valueObj;
                    values.add(value);
                } else {
                    List<String> values = Lists.newArrayList();
                    values.add(valueObj.toString());
                    values.add(value);
                    map.put(key, values);
                }
            }
        } catch (Exception e) {
            log.error("解析url参数失败");
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return map;
    }

    @Override
    public DatasetInfoVO getInfoById(String id) {
        CompletableFuture<DatasetInfoVO> ifPresent = DATASET_INFO_CACHE.getIfPresent(id);
        if (ifPresent != null) {
            try {
                return ifPresent.get();
            } catch (Exception ignored) {
            }
        }
        DatasetEntity entity = this.getByIdFromCache(id);
        DatasetInfoVO datasetInfoVO = BeanConvertUtils.convert(entity, DatasetInfoVO.class);
        HttpDataSetConfig config = (HttpDataSetConfig) entity.getConfig();
        datasetInfoVO.setFields(config.getFieldList());
        datasetInfoVO.setParams(config.getParamsList());
        datasetInfoVO.setExecutionByFrontend(config.getRequestType().equals(FRONTEND));
        DATASET_INFO_CACHE.put(id, CompletableFuture.completedFuture(datasetInfoVO));
        return datasetInfoVO;
    }

    @Override
    public boolean checkBackendExecutionNeeded(String datasetId) {
        DatasetEntity entity = this.getByIdFromCache(datasetId);
        HttpDataSetConfig config = (HttpDataSetConfig) entity.getConfig();
        return !config.getRequestType().equals(FRONTEND);
    }
}