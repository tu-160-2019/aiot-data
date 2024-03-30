package cc.iotkit.openapi.controller;

import cc.iotkit.common.api.Request;
import cc.iotkit.model.InvokeResult;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.openapi.dto.bo.device.OpenapiDeviceBo;
import cc.iotkit.openapi.dto.bo.device.OpenapiSetDeviceServicePropertyBo;
import cc.iotkit.openapi.dto.vo.OpenDeviceInfoVo;
import cc.iotkit.openapi.dto.vo.OpenDevicePropertyVo;
import cc.iotkit.openapi.service.OpenBaseService;
import cc.iotkit.openapi.service.OpenDeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = {"openapi-设备"})
@Slf4j
@RestController
@RequestMapping("/openapi/device")
public class OpenDeviceController {

    @Autowired
    private OpenBaseService openBaseService;

    @Autowired
    private OpenDeviceService openDeviceService;

    @ApiOperation("查询单个设备详情")
    @PostMapping("/v1/detail")
    public DeviceInfo getDetail(@RequestBody @Validated Request<OpenapiDeviceBo> bo) {
        return openDeviceService.getDetail(bo.getData());
    }

    @ApiOperation(value = "单个设备注册")
    @PostMapping("/v1/registerDevice")
    public OpenDeviceInfoVo createDevice(@RequestBody @Validated Request<OpenapiDeviceBo> bo) {
        return openDeviceService.addDevice(bo.getData());
    }

    @ApiOperation("单个设备删除")
    @PostMapping("/v1/deleteDevice")
    public boolean deleteDevice(@Validated @RequestBody Request<OpenapiDeviceBo> bo) {
        return openDeviceService.deleteDevice(bo.getData());
    }

    @ApiOperation(value = "设置设备的属性", notes = "设置设备的属性", httpMethod = "POST")
    @PostMapping("/v1/setDeviceProperty")
    public InvokeResult setProperty(@RequestBody @Validated Request<OpenapiSetDeviceServicePropertyBo> request) {
        return new InvokeResult(openDeviceService.setProperty(request.getData().getProductKey(), request.getData().getDeviceName(), request.getData().getArgs()));
    }

    @ApiOperation("查询指定设备的属性快照")
    @PostMapping("/v1/queryDevicePropertyStatus")
    public OpenDevicePropertyVo getDevicePropertyStatus(@RequestBody @Validated Request<OpenapiDeviceBo> bo) {
        return openDeviceService.getDevicePropertyStatus(bo.getData());
    }
}
