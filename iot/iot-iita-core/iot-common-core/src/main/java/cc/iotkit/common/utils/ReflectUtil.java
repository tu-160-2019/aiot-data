/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.common.utils;


import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectUtil {

    @SneakyThrows
    public static <T> T copyNoNulls(T from, T to, String... fields) {
        List<String> fieldList = Arrays.asList(fields);

        Map<String, Object> map = new HashMap<>();
        new BeanMap(from).forEach((key, value) -> {
            if (value == null) {
                return;
            }
            String field = key.toString();
            if (fields.length == 0 || fieldList.contains(field)) {
                map.put(field, value);
            }
        });
        BeanUtils.populate(to, map);
        return to;
    }

    public static Map<String, ?> toMap(Object bean) {
        Map<String, Object> map = new HashMap<>();
        new BeanMap(bean).forEach((key, value) -> {
            if (key.equals("class")) {
                return;
            }
            String field = key.toString();
            map.put(field, value);
        });
        return map;
    }

}
