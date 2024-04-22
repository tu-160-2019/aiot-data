package com.ai.aigc.controller;

import com.ai.aigc.service.StableDiffusionService;
import lombok.AllArgsConstructor;
import net.srt.api.module.system.StorageApi;
import net.srt.api.module.system.dto.StorageDTO;
import net.srt.framework.common.utils.Result;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@AllArgsConstructor
@RestController
public class SdController {
    @Autowired
    StableDiffusionService stableDiffusionService;

    private final StorageApi storageApi;

    @PreAuthorize("hasAuthority('aidet:text2img')")
    @PostMapping("/text2img")
    public Result<Map> text2img(String promptText, MultipartFile imgFile) {
        try{
            if((promptText==null || promptText.equals("")) && imgFile==null)
                return null;
            Map result = new HashedMap();
            if(promptText!=null && promptText!="" && imgFile!=null) {
                Result<StorageDTO> fileResult = storageApi.upload(imgFile);
                String resultUrl = stableDiffusionService.img2img(fileResult.getData().getPath(), promptText);

                result.put("result", resultUrl);
            } else {
                String resultUrl = stableDiffusionService.text2img(promptText);
                result.put("result", resultUrl);
            }

            return Result.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
