package com.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {
    @Value("${ai.model.path:/bigdata/uplaod}")
    public static String modelPath;

    @Value("${ai.file.prefix}")
    public static String localFilePrefix;

    @Value("${ai.file.domain}")
    public static String domain;

    @Value("${ai.file.prefix}")
    public static String prefix;

    /**
     * 上传文件存储在本地的根路径
     */
    @Value("${ai.file.path}")
    public static String localFilePath;
}
