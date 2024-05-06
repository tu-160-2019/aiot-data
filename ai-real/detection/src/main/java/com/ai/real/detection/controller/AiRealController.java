package com.ai.real.detection.controller;

import lombok.AllArgsConstructor;

import net.srt.api.module.system.dto.StorageDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import net.srt.framework.common.utils.file.FileUploadUtils;
import net.srt.framework.common.utils.file.FileUtils;
import net.srt.framework.common.utils.Result;
import com.ai.real.detection.service.AiRealService;
import net.srt.api.module.system.StorageApi;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
public class AiRealController {
    @Autowired
    AiRealService aiRealService;

    private final StorageApi storageApi;

    @PreAuthorize("hasAuthority('aidet:real:detect')")
    @PostMapping("/realDetect")
    public Result<Map> yolo7(MultipartFile file) {
        try{
            Result<StorageDTO> fileResult = storageApi.upload(file);
            String resultUrl = aiRealService.cameraDetection(fileResult.getData().getUrl());
            String originUrl = fileResult.getData().getUrl();

            Map result = new HashMap();
            result.put("origin", originUrl);
            result.put("result", resultUrl);

            return Result.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
