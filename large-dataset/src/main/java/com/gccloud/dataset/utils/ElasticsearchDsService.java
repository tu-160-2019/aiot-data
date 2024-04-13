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


import com.gccloud.common.utils.JSON;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * es数据源工具类
 *
 * @author hongyang
 * @version 1.0
 * @date 2023/8/30 11:26
 */
public class ElasticsearchDsService {

    /**
     * 根据esDSL查询数据
     *
     * @param host     es服务ip
     * @param port     es服务端口
     * @param username es用户名
     * @param password es密码
     * @param uri      es查询uri
     * @param dsl      es查询dsl
     * @return 查询结果
     * @throws IOException
     */
    public static List<Map<String, Object>> query(String host, int port, String username, String password, String uri, String dsl) throws IOException {
        List<Map<String, Object>> result = new ArrayList<>();
        // 创建基础客户端
        RestClient restClient;
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            // 创建凭证提供者,设置用户名 密码
            BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
            basicCredentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            restClient = RestClient.builder(
                            new HttpHost(host, port, "http"))
                    .setHttpClientConfigCallback(
                            httpClientBuilder -> httpClientBuilder
                                    .setDefaultCredentialsProvider(basicCredentialsProvider))
                    .build();
        } else {
            restClient = RestClient.builder(new HttpHost(host, port, "http")).build();
        }
        // 直接根据DSL查询
        Request request = new Request("POST", uri);
        request.setJsonEntity(dsl);
        Response response = restClient.performRequest(request);
        if (response.getStatusLine().getStatusCode() != 200) {
            return result;
        }
        HttpEntity entity = response.getEntity();
        StringBuilder json = new StringBuilder();
        InputStream content = entity.getContent();
        byte[] bytes = new byte[1024];
        int len = 0;
        while ((len = content.read(bytes)) != -1) {
            json.append(new String(bytes, 0, len));
        }
        // 解析json,获取_source
        JSONObject jsonObject = JSON.parseObject(json.toString());
        JSONObject hits = jsonObject.getJSONObject("hits");
        if (hits == null) {
            return result;
        }
        JSONArray jsonArray = hits.getJSONArray("hits");
        if (jsonArray == null) {
            return result;
        }
        for (Object o : jsonArray) {
            JSONObject source = ((JSONObject) o).getJSONObject("_source");
            result.add(source.toMap());
        }
        return result;
    }


}