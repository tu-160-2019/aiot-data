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

package com.gccloud.common.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * json工具类
 */
@Slf4j
public class JSON {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        OBJECT_MAPPER.registerModule(new JsonOrgModule());

        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.setLocale(Locale.CHINA);
    }

    private JSON() {
    }

    /**
     * 获取jackson操作对象，当该类不满足需求时，可调用原生api进行使用
     *
     * @return
     */
    public static ObjectMapper getInstance() {
        return OBJECT_MAPPER;
    }

    @SneakyThrows
    public static <T> T parseObject(String text, Class<T> clazz) {
        return OBJECT_MAPPER.readValue(text, clazz);
    }

    @SneakyThrows
    public static <T> T parseObject(JSONObject jsonObject, Class<T> clazz) {
        return OBJECT_MAPPER.readValue(jsonObject.toString(), clazz);
    }

    @SneakyThrows
    public static <T> T parseObject(String text, Type type) {
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructType(type);
        return OBJECT_MAPPER.readValue(text, javaType);
    }

    @SneakyThrows
    public static JSONObject parseObject(String text) {
        return OBJECT_MAPPER.readValue(text, JSONObject.class);
    }

    @SneakyThrows
    public static JSONArray parseArray(String text) {
        return OBJECT_MAPPER.readValue(text, JSONArray.class);
    }

    @SneakyThrows
    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        CollectionType listType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
        return OBJECT_MAPPER.readValue(text, listType);
    }

    @SneakyThrows
    public static String toJSONString(Object object) {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

}