package com.ai.detection.controller;

import lombok.AllArgsConstructor;

import net.srt.api.module.system.dto.StorageDTO;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import net.srt.framework.common.utils.file.FileUploadUtils;
import net.srt.framework.common.utils.file.FileUtils;
import net.srt.framework.common.utils.Result;
import com.ai.detection.service.AiService;
import net.srt.api.module.system.StorageApi;

import javax.annotation.Resource;
import java.util.Map;

@AllArgsConstructor
@RestController
public class AiController {
    @Autowired
    AiService aiService;

    private final StorageApi storageApi;

    @PreAuthorize("hasAuthority('aidet:yolo5')")
    @PostMapping("/yolo5")
    public Result<Map> yoloV5(MultipartFile file) {
        try{
            Result<StorageDTO> fileResult = storageApi.upload(file);
            String dec = aiService.yolo5(fileResult.getData().getUrl());

            Map result = new HashedMap();
            result.put("origin", fileResult.getData().getUrl());
            result.put("result", dec);

            return Result.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @PreAuthorize("hasAuthority('aidet:yolo7')")
    @PostMapping("/yolo7")
    public Result<Map> yolo7(MultipartFile file) {
        try{
            Result<StorageDTO> fileResult = storageApi.upload(file);
            String dec = aiService.yolo7(fileResult.getData().getUrl());

            Map result = new HashedMap();
            result.put("origin", fileResult.getData().getUrl());
            result.put("result", dec);

            return Result.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @PreAuthorize("hasAuthority('aidet:yolo8')")
    @PostMapping("/yolo8")
    public Result<Map> yolo8(MultipartFile file) {
        try {
            Result<StorageDTO> fileResult = storageApi.upload(file);
            String dec = aiService.yolo8(fileResult.getData().getUrl());

            Map result = new HashedMap();
            result.put("origin", fileResult.getData().getUrl());
            result.put("result", dec);

            return Result.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
