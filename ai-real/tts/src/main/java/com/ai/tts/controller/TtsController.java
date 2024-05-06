package com.ai.tts.controller;

import com.ai.tts.query.DataTtsQuery;
import com.ai.tts.vo.DataTtsVo;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

import net.srt.api.module.system.dto.StorageDTO;
import net.srt.framework.common.page.PageResult;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import net.srt.framework.common.utils.file.FileUploadUtils;
import net.srt.framework.common.utils.file.FileUtils;
import net.srt.framework.common.utils.Result;
import net.srt.api.module.system.StorageApi;

import com.ai.tts.service.TtsService;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/tts")
public class TtsController {
    @Autowired
    TtsService ttsService;

    private final StorageApi storageApi;

    @GetMapping("page")
    @Operation(summary = "分页")
    public Result<PageResult<DataTtsVo>> page(@Valid DataTtsQuery query) {
        PageResult<DataTtsVo> page = ttsService.page(query);

        return Result.ok(page);
    }

    @DeleteMapping("delete")
    @Operation(summary = "删除")
    public Result<String> delete(@RequestBody List<Long> idList) {
        ttsService.delete(idList);

        return Result.ok();
    }

    @PreAuthorize("hasAuthority('tts:sv2tts')")
    @PostMapping("/sv2tts")
    public Result<Map> sv2tts(@RequestBody DataTtsVo tts) {
        try {
            ttsService.sv2tts(tts.getText());
            return Result.ok();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
