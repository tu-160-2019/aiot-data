package com.ai.detection.controller;

import com.ai.detection.service.AiService;
import com.ai.detection.service.DjlService;
import lombok.AllArgsConstructor;
import net.srt.api.module.system.StorageApi;
import net.srt.api.module.system.dto.StorageDTO;
import net.srt.framework.common.utils.Result;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@AllArgsConstructor
@RestController
public class DjlController {
    @Autowired
    AiService aiService;

    @Autowired
    DjlService djlService;

    private final StorageApi storageApi;

    @PreAuthorize("hasAuthority('aidet:fireSmoke')")
    @PostMapping("/fireSmoke")
    public Result<Map> fireSmoke(MultipartFile file) {
        try {
            Result<StorageDTO> fileResult = storageApi.upload(file);
            String resultUrl = djlService.fireSmoke(fileResult.getData().getUrl());
            String originUrl = fileResult.getData().getUrl();

            Map result = new HashedMap();
            result.put("origin", originUrl);
            result.put("result", resultUrl);

            return Result.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @PreAuthorize("hasAuthority('aidet:reflectiveVest')")
    @PostMapping("/reflectiveVest")
    public Result<Map> reflectiveVest(MultipartFile file) {
        try {
            Result<StorageDTO> fileResult = storageApi.upload(file);
            String resultUrl = djlService.reflectiveVest(fileResult.getData().getUrl());
            String originUrl = fileResult.getData().getUrl();

            Map result = new HashedMap();
            result.put("origin", originUrl);
            result.put("result", resultUrl);

            return Result.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @PreAuthorize("hasAuthority('aidet:vehicleDetec')")
    @PostMapping("/vehicleDetec")
    public Result<Map> vehicle(MultipartFile file) {
        try {
            Result<StorageDTO> fileResult = storageApi.upload(file);
            String resultUrl = djlService.vehicle(fileResult.getData().getUrl());
            String originUrl = fileResult.getData().getUrl();

            Map result = new HashedMap();
            result.put("origin", originUrl);
            result.put("result", resultUrl);

            return Result.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @PreAuthorize("hasAuthority('aidet:camera_facemask')")
    @PostMapping("/cameraFacemask")
    public Result<Map> cameraFacemask(MultipartFile file) {
        try {
            Result<StorageDTO> fileResult = storageApi.upload(file);
            String resultUrl = djlService.cameraFacemask(fileResult.getData().getUrl());
            String originUrl = fileResult.getData().getUrl();

            Map result = new HashedMap();
            result.put("origin", originUrl);
            result.put("result", resultUrl);

            return Result.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
