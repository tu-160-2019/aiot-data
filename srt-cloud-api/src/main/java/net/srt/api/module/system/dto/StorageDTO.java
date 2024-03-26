package net.srt.api.module.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

/**
 * 文件上传
 *
 * @author 阿沐 babamu@126.com
 */
@Data
@Tag(name="文件上传")
public class StorageDTO {
    @Schema(description = "URL")
    private String url;
    @Schema(description = "文件大小")
    private Long size;

}
