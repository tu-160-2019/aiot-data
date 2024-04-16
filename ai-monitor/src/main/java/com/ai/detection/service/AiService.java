package com.ai.detection.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
public interface AiService {

    public String yolo7(String file);

    public String yolo8(String file);
}


