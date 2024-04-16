package net.srt.system.api;

import lombok.AllArgsConstructor;
import net.srt.api.module.system.StorageApi;
import net.srt.api.module.system.dto.StorageDTO;
import net.srt.framework.common.utils.Result;
import net.srt.storage.service.StorageService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件上传
 *
 *  @author 阿沐 babamu@126.com
 */
@RestController
@AllArgsConstructor
public class StorageApiImpl implements StorageApi {
    private final StorageService storageService;

    @Override
    public Result<StorageDTO> upload(MultipartFile file) throws IOException {
        // 是否为空
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        // 上传路径
        String path = storageService.getPath(file.getOriginalFilename());
        // 上传文件
        String url = storageService.upload(file.getBytes(), path);

        // 上传信息
        StorageDTO storage = new StorageDTO();
        storage.setUrl(url);
        storage.setSize(file.getSize());
        storage.setPath(path);

        return Result.ok(storage);
    }
}
