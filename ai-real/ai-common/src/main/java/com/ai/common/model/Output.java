package com.ai.common.model;

import java.util.List;
import java.util.Map;

/**
 * 模型统一输出接口
 */
public interface Output {

    public List<Map<String, Integer>> getLocation();

    public String getName();

    public Integer getClsId();

}
