package com.ai.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Component
public class AiConfig {
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

    /**
     * 目标音色
     */
    @Value("${ai.tts.target.voice}")
    private String ttsVoice;

    /**
     * 合成的语音文件保存目录
     */
    @Value("${ai.tts.output.path}")
    private String ttsOutputPath;
}
