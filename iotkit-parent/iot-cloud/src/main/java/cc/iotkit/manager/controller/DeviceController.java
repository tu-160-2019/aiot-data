/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.manager.controller;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.api.Request;
import cc.iotkit.common.api.Response;
import cc.iotkit.common.excel.utils.ExcelUtil;
import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.manager.dto.bo.device.*;
import cc.iotkit.manager.dto.bo.deviceconfig.DeviceConfigAddBo;
import cc.iotkit.manager.dto.bo.devicegroup.DeviceAddGroupBo;
import cc.iotkit.manager.dto.bo.devicegroup.DeviceGroupBo;
import cc.iotkit.manager.dto.bo.thingmodel.ThingModelMessageBo;
import cc.iotkit.manager.dto.vo.deviceconfig.DeviceConfigVo;
import cc.iotkit.manager.dto.vo.devicegroup.DeviceGroupImportVo;
import cc.iotkit.manager.dto.vo.devicegroup.DeviceGroupVo;
import cc.iotkit.manager.dto.vo.deviceinfo.DeviceInfoImportVo;
import cc.iotkit.manager.dto.vo.deviceinfo.DeviceInfoVo;
import cc.iotkit.manager.dto.vo.deviceinfo.ParentDeviceVo;
import cc.iotkit.manager.dto.vo.thingmodel.ThingModelVo;
import cc.iotkit.manager.service.DeviceCtrlService;
import cc.iotkit.manager.service.IDeviceManagerService;
import cc.iotkit.manager.service.IProductService;
import cc.iotkit.model.InvokeResult;
import cc.iotkit.model.device.DeviceConfig;
import cc.iotkit.model.device.DeviceGroup;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.device.message.DeviceProperty;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.IdUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Api(tags = {"设备"})
@Slf4j
@RestController
@RequestMapping("/device")
public class DeviceController {


    @Autowired
    IProductService productService;

    @Autowired
    private DeviceCtrlService deviceCtrlService;
    @Autowired
    private IDeviceManagerService deviceServiceImpl;


    @ApiOperation(value = "服务调用", notes = "服务调用", httpMethod = "POST")
    @SaCheckPermission("iot:device:ctrl")
    @PostMapping("/service/invoke")
    public InvokeResult invokeService(@RequestBody @Validated Request<ServiceInvokeBo> request) {
        return new InvokeResult(deviceCtrlService.invokeService(request.getData().getDeviceId(), request.getData().getService(), request.getData().getArgs()));
    }

    @ApiOperation(value = "属性获取", notes = "属性获取", httpMethod = "POST")
    @SaCheckPermission("iot:device:ctrl")
    @PostMapping("/service/property/get")
    public InvokeResult invokeServicePropertySet(@RequestBody @Validated Request<GetDeviceServicePorpertyBo> request) {
        return new InvokeResult(deviceCtrlService.getProperty(request.getData().getDeviceId(), request.getData().getPropertyNames(), true));
    }

    @ApiOperation(value = "属性设置", notes = "属性设置", httpMethod = "POST")
    @SaCheckPermission("iot:device:ctrl")
    @PostMapping("/service/property/set")
    public InvokeResult setProperty(@RequestBody @Validated Request<SetDeviceServicePorpertyBo> request) {
        return new InvokeResult(deviceCtrlService.setProperty(request.getData().getDeviceId(), request.getData().getArgs()));
    }

    @ApiOperation(value = "设备列表", notes = "设备列表", httpMethod = "POST")
    @SaCheckPermission("iot:device:query")
    @PostMapping("/list")
    public Paging<DeviceInfoVo> getDevices(@Validated @RequestBody PageRequest<DeviceQueryBo> pageRequest) {
        return deviceServiceImpl.getDevices(pageRequest);
    }

    @ApiOperation(value = "创建设备")
    @SaCheckPermission("iot:device:add")
    @PostMapping("/add")
    public boolean createDevice(@RequestBody @Validated Request<DeviceInfoBo> bo) {
        return deviceServiceImpl.addDevice(bo.getData());
    }

    @ApiOperation(value = "保存设备")
    @SaCheckPermission("iot:device:edit")
    @PostMapping("/save")
    public boolean saveDevice(@RequestBody @Validated Request<DeviceInfoBo> bo) {
        return deviceServiceImpl.saveDevice(bo.getData());
    }

    @ApiOperation(value = "获取子设备", notes = "获取子设备")
    @SaCheckPermission("iot:device:query")
    @PostMapping("/children/list")
    public List<DeviceInfoVo> getChildren(@Validated @RequestBody PageRequest<String> request) {
        String deviceId = request.getData();
        return deviceServiceImpl.selectChildrenPageList(deviceId);
    }

    @ApiOperation("获取网关设备")
    @SaCheckPermission("iot:device:query")
    @PostMapping("/getParentDevices")
    public List<ParentDeviceVo> getParentDevices() {
        return deviceServiceImpl.getParentDevices();
    }

    @ApiOperation("获取设备详情")
    @SaCheckPermission("iot:device:query")
    @PostMapping("/detail")
    public DeviceInfo getDetail(@RequestBody @Validated Request<String> request) {
        return deviceServiceImpl.getDetail(request.getData());
    }

    @ApiOperation("获取设备详情")
    @SaCheckPermission("iot:device:query")
    @PostMapping("/getByPkDn")
    public DeviceInfo getByPkDn(@Validated @RequestBody Request<DeviceQueryByPkDnBo> query) {
        return deviceServiceImpl.getByPkDn(query.getData().getPk(), query.getData().getDn());
    }

    @ApiOperation("删除设备")
    @SaCheckPermission("iot:device:remove")
    @PostMapping("/delete")
    public boolean deleteDevice(@Validated @RequestBody Request<String> request) {
        return deviceServiceImpl.deleteDevice(request.getData());
    }

    @ApiOperation("批量删除设备")
    @SaCheckPermission("iot:device:remove")
    @PostMapping("/batchDelete")
    public boolean batchDelete(@Validated @RequestBody Request<List<String>> request) {
        return deviceServiceImpl.batchDeleteDevice(request.getData());
    }


    /**
     * 导入设备-批量添加设备
     */
    @ApiOperation(value = "导入设备")
    @SaCheckPermission("iot:device:add")
    @PostMapping("/importData")
    public Response importDevice(@RequestPart("file") MultipartFile file, @RequestParam("requestId") String requestId) {
        return new Response(200, deviceServiceImpl.importDevice(file), null, requestId);
    }

    /**
     * 获取导入设备模板
     */
    @ApiOperation("下载设备模板")
    @PostMapping("/exportData")
    public void exportDeviceTemplate(HttpServletResponse response) {
        ExcelUtil.exportExcel(new ArrayList<>(), "设备分组", DeviceInfoImportVo.class, response);
    }

    @ApiOperation("设备物模型日志")
    @SaCheckPermission("iot:deviceLog:query")
    @PostMapping("/deviceLogs/list")
    public Paging<ThingModelMessage> logs(@Validated @RequestBody PageRequest<DeviceLogQueryBo> request) {
        return deviceServiceImpl.logs(request);
    }

    @ApiOperation("获取设备属性历史数据")
    @SaCheckPermission("iot:deviceLog:query")
    @PostMapping("/deviceProperty/log/list")
    public List<DeviceProperty> getPropertyHistory(@Validated @RequestBody
                                                           Request<DevicePropertyLogQueryBo> query) {
        DevicePropertyLogQueryBo data = query.getData();
        String deviceId = data.getDeviceId();
        String name = data.getName();
        long start = data.getStart();
        long end = data.getEnd();
        return deviceServiceImpl.getPropertyHistory(deviceId, name, start, end, 10000);
    }

    @ApiOperation("设备解绑")
    @SaCheckPermission("iot:device:edit")
    @PostMapping("/unbind")
    public boolean unbindDevice(@Validated @RequestBody Request<String> request) {
        return deviceServiceImpl.unbindDevice(request.getData());
    }

    @ApiOperation("获取设备物模型")
    @SaCheckPermission("iot:thingModel:query")
    @PostMapping("/getThingModel")
    public ThingModelVo getThingModel(@Validated @RequestBody Request<String> request) {
        String deviceId = request.getData();
        DeviceInfo deviceInfo = deviceServiceImpl.getDetail(deviceId);
        return productService.getThingModelByProductKey(deviceInfo.getProductKey());
    }

    @ApiOperation("添加标签")
    @SaCheckPermission("iot:device:edit")
    @PostMapping("/tag/add")
    public boolean addTag(@Validated @RequestBody Request<DeviceTagAddBo> bo) {
        return deviceServiceImpl.addTag(bo.getData());
    }

    @ApiOperation("模拟设备上报")
    @SaCheckPermission("iot:device:query")
    @PostMapping("/simulateSend")
    public boolean simulateSend(
            @Validated @RequestBody Request<ThingModelMessageBo> bo) {
        ThingModelMessage message = bo.getData().to(ThingModelMessage.class);
        return deviceServiceImpl.simulateSend(message);
    }

    /**
     * 消费设备信息消息（实时推送设备信息）
     */
    @ApiOperation("消费设备信息消息（实时推送设备信息）")
    @SaCheckPermission("iot:device:query")
    @PostMapping("/consumer")
    public DeferredResult<ThingModelMessage> consumerDeviceInfo(
            @Validated @RequestBody Request<DeviceConsumerBo> bo
    ) {
        DeviceConsumerBo data = bo.getData();
        return deviceServiceImpl.addConsumer(data.getDeviceId(), data.getClientId());
    }

    /**
     * 获取分组列表
     */
    @ApiOperation(value = "获取分组列表")
    @SaCheckPermission("iot:deviceGroup:query")
    @PostMapping("/groups/list")
    public Paging<DeviceGroupVo> getDeviceGroups(
            @Validated @RequestBody PageRequest<DeviceGroupBo> pageRequest) {
        return deviceServiceImpl.selectGroupPageList(pageRequest);
    }

    /**
     * 添加设备分组
     */
    @ApiOperation(value = "添加设备分组")
    @SaCheckPermission("iot:deviceGroup:add")
    @PostMapping("/group/add")
    public boolean addGroup(@Validated @RequestBody Request<DeviceGroupBo> group) {
        return deviceServiceImpl.addGroup(group.getData().to(DeviceGroup.class));
    }


    /**
     * 导入设备分组-批量添加设备分组
     */
    @ApiOperation(value = "导入设备分组")
    @SaCheckPermission("iot:deviceGroup:add")
    @PostMapping("/group/importData")
    public Response importGroup(@RequestPart("file") MultipartFile file, @RequestParam("requestId") String requestId) {
        return new Response(200, deviceServiceImpl.importGroup(file), null, requestId);
    }

    /**
     * 获取导入模板
     */
    @ApiOperation("下载设备分组模板")
    @PostMapping("/group/exportData")
    public void exportGroupTemplate(HttpServletResponse response) {
        ExcelUtil.exportExcel(new ArrayList<>(), "设备分组", DeviceGroupImportVo.class, response);
    }

    /**
     * 修改设备分组
     */
    @ApiOperation(value = "修改设备分组")
    @SaCheckPermission("iot:deviceGroup:edit")
    @PostMapping("/group/edit")
    public boolean editGroup(@RequestBody @Validated Request<DeviceGroupBo> bo) {
        return deviceServiceImpl.updateGroup(bo.getData());

    }

    /**
     * 删除分组
     */
    @ApiOperation(value = "删除分组")
    @SaCheckPermission("iot:deviceGroup:remove")
    @PostMapping("/group/delete")
    public boolean deleteGroup(@Validated @RequestBody Request<String> request) {
        String id = request.getData();
        return deviceServiceImpl.deleteGroup(id);
    }

    /**
     * 清空组下所有设备
     */
    @ApiOperation(value = "清空组下所有设备")
    @SaCheckPermission("iot:deviceGroup:remove")
    @PostMapping("/group/clear")
    public boolean clearGroup(@Validated @RequestBody Request<String> request) {
        String id = request.getData();
        return deviceServiceImpl.clearGroup(id);
    }

    /**
     * 添加设备到组
     */
    @ApiOperation(value = "添加设备到组")
    @SaCheckPermission("iot:deviceGroup:edit")
    @PostMapping("/group/addDevices")
    public boolean addToGroup(@Validated @RequestBody Request<DeviceAddGroupBo> bo) {
        return deviceServiceImpl.addDevice2Group(bo.getData());
    }

    /**
     * 将设备从组中移除
     */
    @ApiOperation(value = "将设备从组中移除")
    @SaCheckPermission("iot:deviceGroup:edit")
    @PostMapping("/group/removeDevices")
    public boolean removeDevices(@Validated @RequestBody Request<DeviceAddGroupBo> bo) {
        DeviceAddGroupBo data = bo.getData();
        return deviceServiceImpl.removeDevices(data.getGroup(), data.getDevices());
    }

    /**
     * 保存设备配置
     */
    @ApiOperation(value = "保存设备配置")
    @SaCheckPermission("iot:device:edit")
    @PostMapping("/config/save")
    public boolean saveConfig(@Validated @RequestBody Request<DeviceConfigAddBo> request) {
        DeviceConfig data = request.getData().to(DeviceConfig.class);
        return deviceServiceImpl.saveConfig(data);
    }

    /**
     * 获取设备配置
     */
    @ApiOperation(value = "获取设备配置")
    @SaCheckPermission("iot:device:query")
    @PostMapping("/config/get")
    public DeviceConfigVo getConfig(@Validated @RequestBody Request<String> request) {
        String deviceId = request.getData();
        return deviceServiceImpl.getConfig(deviceId);
    }

    /**
     * 设备配置下发
     */
    @ApiOperation(value = "设备配置下发")
    @SaCheckPermission("iot:device:ctrl")
    @PostMapping("/config/send")
    public InvokeResult sendConfig(@Validated @RequestBody Request<String> bo) {
        String deviceId = bo.getData();
        return new InvokeResult(deviceCtrlService.sendConfig(deviceId));
    }

}
