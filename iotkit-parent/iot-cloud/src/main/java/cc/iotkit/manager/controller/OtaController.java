package cc.iotkit.manager.controller;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.api.Request;
import cc.iotkit.common.web.core.BaseController;
import cc.iotkit.manager.dto.bo.ota.DeviceOtaDetailBo;
import cc.iotkit.manager.dto.bo.ota.DeviceOtaInfoBo;
import cc.iotkit.manager.dto.bo.ota.DeviceUpgradeBo;
import cc.iotkit.manager.dto.bo.ota.OtaPackageBo;
import cc.iotkit.manager.dto.vo.ota.DeviceOtaDetailVo;
import cc.iotkit.manager.dto.vo.ota.DeviceOtaInfoVo;
import cc.iotkit.manager.dto.vo.ota.DeviceUpgradeVo;
import cc.iotkit.manager.dto.vo.ota.OtaPackageUploadVo;
import cc.iotkit.manager.service.OtaService;
import cc.iotkit.model.ota.OtaPackage;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @Author: 石恒
 * @Date: 2023/5/19 20:42
 * @Description:
 */
@Api(tags = {"ota升级管理"})
@Slf4j
@RestController
@RequestMapping("/ota")
public class OtaController extends BaseController {

    @Resource
    private OtaService otaService;

    @ApiOperation("升级包上传")
    @SaCheckPermission("iot:ota:add")
    @PostMapping(value = "/package/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public OtaPackageUploadVo packageUpload(@RequestPart("file") MultipartFile file, @RequestParam("requestId") String requestId) throws Exception {
        if (ObjectUtil.isNull(file)) {
            fail("上传文件不能为空");
        }
        return otaService.uploadFile(file);
    }

    @ApiOperation("新增升级包")
    @SaCheckPermission("iot:ota:add")
    @PostMapping("/package/add")
    public OtaPackage packageAdd(@RequestBody @Valid Request<OtaPackageBo> request) {
        return otaService.addOtaPackage(request.getData());
    }

    @ApiOperation("删除升级包")
    @SaCheckPermission("iot:ota:remove")
    @PostMapping("/package/delById")
    public Boolean delPackageById(@RequestBody @Valid Request<Long> request) {
        return otaService.delOtaPackageById(request.getData());
    }

    @ApiOperation("升级包列表")
    @SaCheckPermission("iot:ota:query")
    @PostMapping("/package/getList")
    public Paging<OtaPackage> packageList(@RequestBody @Validated PageRequest<OtaPackage> request) {
        return otaService.getOtaPackagePageList(request);
    }

    @ApiOperation("OTA升级")
    @SaCheckPermission("iot:ota:upgrade")
    @PostMapping("/device/upgrade")
    public DeviceUpgradeVo deviceUpgrade(@RequestBody Request<DeviceUpgradeBo> request) {
        String result = otaService.startUpgrade(request.getData().getOtaId(), request.getData().getDeviceIds());
        return DeviceUpgradeVo.builder().result(result).build();
    }

    @ApiOperation("设备升级结果查询")
    @SaCheckPermission("iot:ota:query")
    @PostMapping("/device/detail")
    public Paging<DeviceOtaDetailVo> otaDeviceDetail(@RequestBody PageRequest<DeviceOtaDetailBo> request) {
        return otaService.otaDeviceDetail(request);
    }

    @ApiOperation("设备升级批次查询")
    @SaCheckPermission("iot:ota:query")
    @PostMapping("/device/info")
    public Paging<DeviceOtaInfoVo> otaDeviceInfo(@RequestBody PageRequest<DeviceOtaInfoBo> request) {
        return otaService.otaDeviceInfo(request);
    }

    @ApiOperation("ota升级测试")
    @PostMapping("/testStartUpgrade")
    public void testStartUpgrade(@RequestBody Request<Void> request) {
        otaService.testStartUpgrade();
    }

}
