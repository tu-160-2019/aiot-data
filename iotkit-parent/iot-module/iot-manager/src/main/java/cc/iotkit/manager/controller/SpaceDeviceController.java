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
import cc.iotkit.common.constant.Constants;
import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.satoken.utils.AuthUtil;
import cc.iotkit.common.satoken.utils.LoginHelper;
import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.data.manager.ICategoryData;
import cc.iotkit.data.manager.IDeviceInfoData;
import cc.iotkit.data.manager.IUserInfoData;
import cc.iotkit.manager.dto.bo.device.ServiceInvokeBo;
import cc.iotkit.manager.dto.bo.device.SetDeviceServicePorpertyBo;
import cc.iotkit.manager.dto.bo.ruleinfo.RuleInfoBo;
import cc.iotkit.manager.dto.vo.product.ProductVo;
import cc.iotkit.manager.dto.vo.ruleinfo.RuleInfoVo;
import cc.iotkit.manager.dto.vo.thingmodel.ThingModelVo;
import cc.iotkit.manager.model.vo.FindDeviceVo;
import cc.iotkit.manager.model.vo.SpaceDeviceVo;
import cc.iotkit.manager.service.*;
import cc.iotkit.model.InvokeResult;
import cc.iotkit.model.UserInfo;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.product.Category;
import cc.iotkit.model.space.Home;
import cc.iotkit.model.space.Space;
import cc.iotkit.model.space.SpaceDevice;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Api(tags = {"空间设备"})
@RestController
@RequestMapping("/space")
public class SpaceDeviceController {

    @Autowired
    private ISpaceDeviceService spaceDeviceService;
    @Autowired
    @Qualifier("deviceInfoDataCache")
    private IDeviceInfoData deviceInfoData;
    @Autowired
    private IProductService productService;
    @Autowired
    @Qualifier("categoryDataCache")
    private ICategoryData categoryData;
    @Autowired
    private ISpaceService spaceService;
    @Autowired
    private IHomeService homeService;
    @Autowired
    private DataOwnerService dataOwnerService;
    @Autowired
    private IRuleEngineService ruleEngineService;
    @Autowired
    private IUserInfoData userInfoData;
    @Autowired
    private IDeviceManagerService deviceServiceImpl;
    //赋予应用端设备的服务和属性设置，关于应用的接口及相关权限设计后续完善，先打通链路
    @Autowired
    private DeviceCtrlService deviceCtrlService;

    /**
     * 我最近使用的设备列表
     */
    @PostMapping(Constants.API_SPACE.RECENT_DEVICES)
    public List<SpaceDeviceVo> getMyRecentDevices() {//TODO 老接口
//        List<SpaceDevice> spaceDevices = spaceDeviceService.findByUserIdAndCollect(LoginHelper.getUserId(),true);
//        return spaceDevices.stream().map((this::parseSpaceDevice)).collect(Collectors.toList());
        return null;
    }

    /**
     * 获取用户收藏设备列表
     */
    @PostMapping(Constants.API_SPACE.GET_COLLECT_DEVICES)
    public List<SpaceDeviceVo> getCollectDevices() {
        Home home = homeService.findByUserIdAndCurrent(LoginHelper.getUserId(), true);
        List<SpaceDevice> spaceDevices = spaceDeviceService.findByHomeIdAndCollect(home.getId(), true);
        return spaceDevices.stream().map((this::parseSpaceDevice)).collect(Collectors.toList());
    }

    /**
     * 收藏/取消收藏设备
     */
    @PostMapping(Constants.API_SPACE.COLLECT_DEVICE)
    public void collectDevice(@RequestBody @Validated Request<SpaceDevice> request) {
        SpaceDevice spaceDevice = request.getData();
        SpaceDevice oldSpaceDevice = spaceDeviceService.findByDeviceId(spaceDevice.getDeviceId());
        oldSpaceDevice.setCollect(spaceDevice.getCollect());
        spaceDeviceService.save(oldSpaceDevice);
    }

    /**
     * 我的空间设备列表-按空间获取
     *
     * @param request
     */
    @PostMapping("/getSpaceDevices")
    public List<SpaceDeviceVo> getMyDevices(@RequestBody @Validated Request<Long> request) {
        List<SpaceDevice> spaceDevices;
        Long spaceId = request.getData();
        if ("all".equals(spaceId)) {
            //全部设备
            Home home = homeService.findByUserIdAndCurrent(LoginHelper.getUserId(), true);
            spaceDevices = spaceDeviceService.findByHomeId(home.getId());
        } else {
            //按空间获取
            spaceDevices = spaceDeviceService.findBySpaceId(spaceId);
        }
        return spaceDevices.stream().map((this::parseSpaceDevice)).collect(Collectors.toList());
    }

    private SpaceDeviceVo parseSpaceDevice(SpaceDevice sd) {
        DeviceInfo device = deviceInfoData.findByDeviceId(sd.getDeviceId());
        Space space = spaceService.findById(sd.getSpaceId());
        ProductVo product = productService.findByProductKey(device.getProductKey());
        ThingModelVo tm = productService.getThingModelByProductKey(device.getProductKey());
        Map<String, Object> property = new HashMap<>();
        if (tm != null) {
            tm.getModel().setEvents(null);
            property = JsonUtils.parseObject(JsonUtils.toJsonString(tm.getModel()), Map.class);
        }
        Category category = categoryData.findById(product.getCategory());
        DeviceInfo.State state = device.getState();

        return SpaceDeviceVo.builder()
                .id(sd.getId())
                .deviceId(sd.getDeviceId())
                .deviceName(device.getDeviceName())
                .name(sd.getName())
                .spaceId(sd.getSpaceId())
                .spaceName(space.getName())
                .productKey(device.getProductKey())
                .productName(product.getName())
                .category(product.getCategory())
                .categoryName(category.getName())
                .picUrl(product.getImg())
                .online(state != null && state.isOnline())
                .property(property)
                .collect(sd.getCollect())
                .build();
    }

    /**
     * 获取用户所有设备列表
     */
    @PostMapping("/{userId}/devices")
    public List<SpaceDeviceVo> getDevices(@PathVariable("userId") Long userId) {
        Home curHome = homeService.findByUserIdAndCurrent(userId, true);
        List<SpaceDevice> spaceDevices = spaceDeviceService.findByHomeId(curHome.getId());
        return spaceDevices.stream().map((this::parseSpaceDevice)).collect(Collectors.toList());
    }

    /**
     * 搜索未添加过的设备
     */
    @PostMapping(Constants.API_SPACE.FIND_DEVICE)
    public List<FindDeviceVo> findDevice(@RequestBody @Validated Request<String> request) {
        String mac = request.getData();
        if (StringUtils.isBlank(mac)) {
            throw new BizException(ErrCode.DATA_BLANK);
        }

        if (mac.trim().length() < 3) {
            throw new BizException(ErrCode.DATA_LENGTH_ERROR);
        }

        List<FindDeviceVo> findDeviceVos = new ArrayList<>();
        DeviceInfo query=new DeviceInfo();
        query.setDeviceName(mac);
        List<DeviceInfo> devices = deviceInfoData.findAllByCondition(query);
        if(devices == null){
            return findDeviceVos;
        }
        //查找网关下子设备
//        List<DeviceInfo> subDevices = new ArrayList<>();
//        if (findDevice.getParentId() == null) {//如果是网关设备
//            subDevices = deviceInfoData.findByParentId(findDevice.getDeviceId());
//        }
//        List<DeviceInfo> devices = new ArrayList<>(subDevices);

        //查找空间设备
        for (DeviceInfo device : devices) {
            SpaceDevice spaceDevice = spaceDeviceService.findByDeviceId(device.getDeviceId());
            if (spaceDevice == null) {
                //没有被其它人占用
                findDeviceVos.add(getFindDeviceVo(device));
            }
        }
        return findDeviceVos;
    }

    private FindDeviceVo getFindDeviceVo(DeviceInfo device) {
        FindDeviceVo findDeviceVo = FindDeviceVo.builder()
                .deviceId(device.getDeviceId())
                .deviceName(device.getDeviceName())
                .productKey(device.getProductKey())
                .build();

        ProductVo product = productService.findByProductKey(device.getProductKey());
        Category category = categoryData.findById(product.getCategory());
        findDeviceVo.setProductName(product.getName());
        findDeviceVo.setProductImg(product.getImg());
        findDeviceVo.setCategoryName(category.getName());
        return findDeviceVo;
    }

    @ApiOperation("获取设备详情")
    @PostMapping("/detail")
    public DeviceInfo getDetail(@RequestBody @Validated Request<String> request) {
        return deviceServiceImpl.getDetail(request.getData());
    }

    @ApiOperation("保存规则")
    @PostMapping("/saveRuleEngine")
    public boolean saveRuleEngine(@RequestBody @Validated Request<RuleInfoBo> ruleInfoBo) {
        return ruleEngineService.saveRule(ruleInfoBo.getData());
    }

    @ApiOperation("删除规则")
    @PostMapping("/delRuleEngine")
    public boolean delRuleEngine(@Validated @RequestBody Request<String> request) {
        String ruleId = request.getData();
        return ruleEngineService.deleteRule(ruleId);
    }

    @ApiOperation("停止规则")
    @PostMapping("/stopRuleEngine")
    public boolean stopRuleEngine(@Validated @RequestBody Request<String> request) {
        String ruleId = request.getData();
        return ruleEngineService.pauseRule(ruleId);
    }

    @ApiOperation("恢复规则")
    @PostMapping("/startRuleEngine")
    public boolean startRuleEngine(@Validated @RequestBody Request<String> request) {
        String ruleId = request.getData();
        return ruleEngineService.resumeRule(ruleId);
    }

    @ApiOperation("规则列表")
    @PostMapping("/ruleEngineList")
    public Paging<RuleInfoVo> ruleEngineList(@Validated @RequestBody PageRequest<RuleInfoBo> request) {
        return ruleEngineService.selectPageList(request);
    }

    @ApiOperation("调用设备服务")
    @PostMapping("/invokeService")
    public InvokeResult invokeService(@RequestBody @Validated Request<ServiceInvokeBo> request) {
        return new InvokeResult(deviceCtrlService.invokeService(request.getData().getDeviceId(), request.getData().getService(), request.getData().getArgs()));
    }

    @ApiOperation(value = "设备属性设置")
    @PostMapping("/setProperty")
    public InvokeResult setProperty(@RequestBody @Validated Request<SetDeviceServicePorpertyBo> request) {
        return new InvokeResult(deviceCtrlService.setProperty(request.getData().getDeviceId(), request.getData().getArgs()));
    }

    /**
     * REMOVE_DEVICE
     * 往指定房间中添加设备
     */
    @PostMapping(Constants.API_SPACE.ADD_DEVICE)
    public void addDevice(@RequestBody @Validated Request<SpaceDevice> request) {
        SpaceDevice device = request.getData();
        String deviceId = device.getDeviceId();
        DeviceInfo deviceInfo = deviceInfoData.findByDeviceId(deviceId);
        if (deviceInfo == null) {
            throw new BizException(ErrCode.DEVICE_NOT_FOUND);
        }
        Long spaceId = device.getSpaceId();
        Space space = spaceService.findById(spaceId);
        if (space == null) {
            throw new BizException(ErrCode.SPACE_NOT_FOUND);
        }

        SpaceDevice oldSpaceDevice = spaceDeviceService.findByDeviceId(deviceId);
        if (oldSpaceDevice != null) {
            throw new BizException(ErrCode.DEVICE_ALREADY);
        }

        SpaceDevice spaceDevice = SpaceDevice.builder()
                .deviceId(deviceId)
                .spaceId(spaceId)
                .deviceId(deviceId)
                .name(device.getName())
                .homeId(space.getHomeId())
                .collect(false)
                .build();
        spaceDeviceService.save(spaceDevice);

        //更新设备子用户列表
        List<String> subUid = deviceInfo.getSubUid();
        if (subUid == null) {
            subUid = new ArrayList<>();
            deviceInfo.setSubUid(subUid);
        }

        String uid = AuthUtil.getUserId();
        UserInfo userInfo = userInfoData.findById(Long.valueOf(uid));
        if (userInfo == null) {
            throw new BizException(ErrCode.USER_NOT_FOUND);
        }
        if (!subUid.contains(uid)) {
            subUid.add(uid);
        }

        //更新设备标签，标识设备是用的哪个第三方平台
        List<String> platforms = userInfo.getUsePlatforms();
        Map<String, DeviceInfo.Tag> tags = deviceInfo.getTag();
        for (String platform : platforms) {
            Constants.ThirdPlatform thirdPlatform = Constants.ThirdPlatform.valueOf(platform);
            tags.put(platform, new DeviceInfo.Tag(platform, thirdPlatform.desc, "是"));
        }

        deviceInfoData.save(deviceInfo);
    }

    /**
     * 移除房间中的设备
     */
    @PostMapping(Constants.API_SPACE.REMOVE_DEVICE)
    public void removeDevice(@RequestBody @Validated Request<String> request) {
        SpaceDevice spaceDevice = spaceDeviceService.findByDeviceId(request.getData());
        if (spaceDevice == null) {
            throw new BizException(ErrCode.SPACE_DEVICE_NOT_FOUND);
        }

        spaceDeviceService.deleteById(spaceDevice.getId());
        DeviceInfo deviceInfo = deviceInfoData.findByDeviceId(request.getData());
        UserInfo userInfo = userInfoData.findById(LoginHelper.getUserId());
        if (userInfo == null) {
            throw new BizException(ErrCode.USER_NOT_FOUND);
        }

        List<String> platforms = userInfo.getUsePlatforms();
        List<String> subUid = deviceInfo.getSubUid();
        subUid.remove(LoginHelper.getUserId() + "");
        //删除设备标签
        for (String platform : platforms) {
            deviceInfo.getTag().remove(platform);
        }

        deviceInfoData.save(deviceInfo);
    }

    /**
     * 保存房间设备信息
     */
    @PostMapping(Constants.API_SPACE.SAVE_DEVICE)
    public void saveDevice(SpaceDevice spaceDevice) {
        SpaceDevice oldData = spaceDeviceService.findById(spaceDevice.getId());
        if (oldData == null) {
            throw new BizException(ErrCode.SPACE_DEVICE_NOT_FOUND);
        }
        oldData.setName(spaceDevice.getName());
        oldData.setSpaceId(spaceDevice.getSpaceId());
        spaceDeviceService.save(oldData);
    }

    /**
     * 获取房间中指定设备信息
     */
    @PostMapping(Constants.API_SPACE.GET_DEVICE)
    public SpaceDeviceVo getSpaceDevice(@PathVariable("deviceId") String deviceId) {
        SpaceDevice spaceDevice = spaceDeviceService.findByDeviceId(deviceId);
        return parseSpaceDevice(spaceDevice);
    }

    /**
     * 设置设备的第三方平台openUid
     * 如：小度接入使用的openUid
     */
    @PostMapping(Constants.API_SPACE.SET_OPEN_UID)
    public void setOpenUid(String deviceId, String platform, String openUid) {
        SpaceDevice spaceDevice = spaceDeviceService.findByDeviceId(deviceId);
        if (spaceDevice == null) {
            throw new BizException(ErrCode.SPACE_DEVICE_NOT_FOUND);
        }

        //找到设备
        DeviceInfo deviceInfo = deviceInfoData.findByDeviceId(deviceId);
        Map<String, DeviceInfo.Tag> tags = deviceInfo.getTag();
        String openUidName = platform + "OpenUid";
        //给设备添加对应平台openUid的设备标签
        Constants.ThirdOpenUid thirdOpenUid = Constants.ThirdOpenUid.valueOf(openUidName);
        tags.put(openUidName, new DeviceInfo.Tag(openUidName, thirdOpenUid.desc, openUid));
        deviceInfoData.save(deviceInfo);
    }
}
