package cc.iotkit.system.controller;


import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.api.Request;
import cc.iotkit.common.log.annotation.Log;
import cc.iotkit.common.log.enums.BusinessType;
import cc.iotkit.common.oss.core.OssClient;
import cc.iotkit.common.oss.factory.OssFactory;
import cc.iotkit.common.validate.QueryGroup;
import cc.iotkit.common.web.core.BaseController;
import cc.iotkit.system.dto.bo.SysOssBo;
import cc.iotkit.system.dto.vo.SysOssUploadVo;
import cc.iotkit.system.dto.vo.SysOssVo;
import cc.iotkit.system.service.ISysOssService;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import com.amazonaws.services.s3.model.ObjectMetadata;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

/**
 * 文件上传 控制层
 *
 * @author Lion Li
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/resource/oss")
public class SysOssController extends BaseController {

    private final ISysOssService ossService;

    /**
     * 查询OSS对象存储列表
     */
    @SaCheckPermission("system:oss:list")
    @ApiOperation(value = "查询OSS对象存储列表", notes = "查询OSS对象存储列表")
    @PostMapping("/list")
    public Paging<SysOssVo> list(@Validated(QueryGroup.class) @RequestBody PageRequest<SysOssBo> query) {
        return ossService.queryPageList(query);
    }

    /**
     * 查询OSS对象基于id串
     */
    @ApiOperation(value = "查询OSS对象基于id串", notes = "查询OSS对象基于id串")
    @SaCheckPermission("system:oss:list")
    @PostMapping("/listByIds")
    public List<SysOssVo> listByIds(@Validated @RequestBody Request<List<Long>> bo) {
        return ossService.listByIds(bo.getData());
    }

    /**
     * 上传OSS对象存储
     *
     * @param file 文件
     */
    @ApiOperation(value = "上传OSS对象存储", notes = "上传OSS对象存储")
    @SaCheckPermission("system:oss:upload")
    @Log(title = "OSS对象存储", businessType = BusinessType.INSERT)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SysOssUploadVo upload(@RequestPart("file") MultipartFile file, @RequestParam("requestId") String requestId) {
        if (ObjectUtil.isNull(file)) {
            fail("上传文件不能为空");
        }
        SysOssVo oss = ossService.upload(file);
        SysOssUploadVo uploadVo = new SysOssUploadVo();
        uploadVo.setUrl(oss.getUrl());
        uploadVo.setFileName(oss.getOriginalName());
        uploadVo.setOssId(oss.getId().toString());
        return uploadVo;
    }

    /**
     * 下载OSS对象
     */
    @SaCheckPermission("system:oss:download")
    @PostMapping("/downloadById")
    @ApiOperation(value = "下载OSS对象", notes = "下载OSS对象")
    public ResponseEntity<StreamingResponseBody> download(@RequestBody @Validated Request<Long> bo) throws IOException {
        SysOssVo ossVo = ossService.getById(bo.getData());
        OssClient storage = OssFactory.instance();
        ObjectMetadata objectMetadata = storage.getObjectMetadata(ossVo.getUrl());
        InputStream objectContent = storage.getObjectContent(ossVo.getUrl());
        return ResponseEntity
                .ok()
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header("download-filename", ossVo.getFileName())
                .contentLength(objectMetadata.getContentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(out -> IoUtil.copy(objectContent, out));
    }

    /**
     * 删除OSS对象存储
     */
    @ApiOperation(value = "删除OSS对象存储", notes = "删除OSS对象存储")
    @SaCheckPermission("system:oss:remove")
    @Log(title = "OSS对象存储", businessType = BusinessType.DELETE)
    @PostMapping("/delete")
    public void remove(@Validated @RequestBody Request<List<Long>> bo) {
        ossService.deleteWithValidByIds(bo.getData(), true);
    }

}
