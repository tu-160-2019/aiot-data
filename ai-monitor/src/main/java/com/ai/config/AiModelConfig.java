package com.ai.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class AiModelConfig {
    @Value("${ai.model.path:/bigdata/uplaod}")
    private String modelPath;

    @Value("${ai.file.domain}")
    private String domain;

    @Value("${ai.file.prefix}")
    private String prefix;

    /**
     * 上传文件存储在本地的路径
     */
    @Value("${ai.file.path}")
    private String path;
}
