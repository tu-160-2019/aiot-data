package cc.iotkit.manager.service.impl;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.constant.Constants;
import cc.iotkit.common.enums.ErrCode;
import cc.iotkit.common.excel.core.ExcelResult;
import cc.iotkit.common.excel.utils.ExcelUtil;
import cc.iotkit.common.exception.BizException;
import cc.iotkit.common.satoken.utils.AuthUtil;
import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.common.utils.*;
import cc.iotkit.data.manager.IDeviceConfigData;
import cc.iotkit.data.manager.IDeviceGroupData;
import cc.iotkit.data.manager.IDeviceInfoData;
import cc.iotkit.data.manager.IProductData;
import cc.iotkit.manager.dto.bo.device.DeviceInfoBo;
import cc.iotkit.manager.dto.bo.device.DeviceLogQueryBo;
import cc.iotkit.manager.dto.bo.device.DeviceQueryBo;
import cc.iotkit.manager.dto.bo.device.DeviceTagAddBo;
import cc.iotkit.manager.dto.bo.devicegroup.DeviceAddGroupBo;
import cc.iotkit.manager.dto.bo.devicegroup.DeviceGroupBo;
import cc.iotkit.manager.dto.vo.deviceconfig.DeviceConfigVo;
import cc.iotkit.manager.dto.vo.devicegroup.DeviceGroupImportVo;
import cc.iotkit.manager.dto.vo.devicegroup.DeviceGroupVo;
import cc.iotkit.manager.dto.vo.deviceinfo.DeviceInfoImportVo;
import cc.iotkit.manager.dto.vo.deviceinfo.DeviceInfoVo;
import cc.iotkit.manager.dto.vo.deviceinfo.ParentDeviceVo;
import cc.iotkit.manager.listener.DeviceGroupImportListener;
import cc.iotkit.manager.listener.DeviceInfoImportListener;
import cc.iotkit.manager.service.DataOwnerService;
import cc.iotkit.manager.service.DeferredDataConsumer;
import cc.iotkit.manager.service.DeviceCtrlService;
import cc.iotkit.manager.service.IDeviceManagerService;
import cc.iotkit.model.device.DeviceConfig;
import cc.iotkit.model.device.DeviceGroup;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.device.message.DeviceProperty;
import cc.iotkit.model.product.Product;
import cc.iotkit.mq.MqProducer;
import cc.iotkit.temporal.IDevicePropertyData;
import cc.iotkit.temporal.IThingModelMessageData;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: jay
 * @Date: 2023/5/31 11:06
 * @Version: V1.0
 * @Description: 设备服务实现
 */

@Service
public class DeviceManagerServiceImpl implements IDeviceManagerService {

    @Autowired
    private DeviceCtrlService deviceCtrlService;

    @Autowired
    @Qualifier("deviceInfoDataCache")
    private IDeviceInfoData deviceInfoData;
    @Autowired
    @Qualifier("productDataCache")
    private IProductData productData;
    @Autowired
    private DataOwnerService dataOwnerService;

    @Lazy
    @Autowired
    private IThingModelMessageData thingModelMessageData;
    @Lazy
    @Autowired
    private IDevicePropertyData devicePropertyData;
    @Autowired
    DeferredDataConsumer deferredDataConsumer;
    @Autowired
    private IDeviceGroupData deviceGroupData;
    @Autowired
    private IDeviceConfigData deviceConfigData;

    @Autowired
    private MqProducer<ThingModelMessage> producer;

    @Override
    public Paging<DeviceInfoVo> getDevices(PageRequest<DeviceQueryBo> pageRequest) {
        DeviceQueryBo query = pageRequest.getData();

        String uid = "";
        String subUid = "";

        String pk = query.getProductKey();
        //关键字查询
        String keyword = query.getKeyword();
        String group = query.getGroup();
        Boolean online = query.getOnline();

        Paging<DeviceInfoVo> result = MapstructUtils.convert(deviceInfoData.findByConditions(uid, subUid, pk, group,
                online, keyword, pageRequest.getPageNum(), pageRequest.getPageSize()), DeviceInfoVo.class);
        for (DeviceInfoVo row : result.getRows()) {
            row.setProduct(productData.findByProductKey(row.getProductKey()));
        }
        return result;
    }

    @Override
    public boolean addDevice(DeviceInfoBo deviceInfo) {

        String productKey = deviceInfo.getProductKey();
        String deviceName = deviceInfo.getDeviceName();
        String parentId = deviceInfo.getParentId();

        Product product = productData.findByProductKey(productKey);
        if (product == null) {
            throw new BizException(ErrCode.PRODUCT_NOT_FOUND);
        }
        //同产品不可重复设备名
        DeviceInfo deviceRepetition = deviceInfoData.findByDeviceName(deviceName);
        if (deviceRepetition != null) {
            throw new BizException(ErrCode.MODEL_DEVICE_ALREADY);
        }
        //生成设备密钥
        String chars = "ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678";
        int maxPos = chars.length();
        StringBuilder secret = new StringBuilder();
        for (var i = 0; i < 16; i++) {
            secret.append(chars.charAt((int) Math.floor(Math.random() * maxPos)));
        }

        DeviceInfo device = new DeviceInfo();
        device.setId(DeviceUtil.newDeviceId(deviceName));
        device.setUid(product.getUid());
        device.setDeviceId(device.getId());
        device.setProductKey(productKey);
        device.setDeviceName(deviceName);
        device.setSecret(secret.toString());
        device.setState(new DeviceInfo.State(false, null, null));
        device.setLocate(new DeviceInfo.Locate(deviceInfo.getLongitude(), deviceInfo.getLatitude()));
        device.setCreateAt(System.currentTimeMillis());
        if (StringUtils.isNotBlank(parentId)) {
            device.setParentId(parentId);
        }
        deviceInfoData.save(device);
        return true;
    }

    @Override
    public List<DeviceInfoVo> selectChildrenPageList(String deviceId) {
        DeviceInfo deviceInfo = deviceInfoData.findByDeviceId(deviceId);
        if (deviceInfo == null) {
            throw new BizException(ErrCode.DEVICE_NOT_FOUND);
        }

        dataOwnerService.checkOwner(deviceInfo);
        List<DeviceInfoVo> list = MapstructUtils.convert(deviceInfoData.findByParentId(deviceId), DeviceInfoVo.class);
        for (DeviceInfoVo row : list) {
            row.setProduct(productData.findByProductKey(row.getProductKey()));
        }
        return list;
    }

    @Override
    public List<ParentDeviceVo> getParentDevices() {
        String uid = "";
        List<ParentDeviceVo> pdv = null;
        if (!AuthUtil.isAdmin()) {
            uid = AuthUtil.getUserId();
        }
        List<DeviceInfo> ret = deviceInfoData.findByProductNodeType(uid);
        if (!ret.isEmpty()) {
            pdv = ret.stream().map(r -> ParentDeviceVo.builder().id(r.getId()).deviceName(r.getDeviceName()).build()).collect(Collectors.toList());
        }
        return pdv;
    }

    @Override
    public DeviceInfo getDetail(String deviceId) {
        DeviceInfo deviceInfo = deviceInfoData.findByDeviceId(deviceId);
//        dataOwnerService.checkOwner(deviceInfo);
        deviceInfo.setProperty(deviceInfoData.getProperties(deviceId));
        return deviceInfo;
    }

    @Override
    public DeviceInfo getByPkDn(String pk, String dn) {
        return dataOwnerService.checkOwner(
                deviceInfoData.findByDeviceName(dn));
    }

    @Override
    public boolean deleteDevice(String deviceId) {

        deviceId = getDetail(deviceId).getDeviceId();
        deviceInfoData.deleteById(deviceId);
        return true;
    }

    @Override
    public boolean batchDeleteDevice(List<String> ids) {
        deviceInfoData.deleteByIds(ids);
        return true;
    }

    @Override
    public Paging<ThingModelMessage> logs(PageRequest<DeviceLogQueryBo> request) {
        DeviceLogQueryBo data = request.getData();
        return thingModelMessageData.findByTypeAndIdentifier(data.getDeviceId(), data.getType(), data.getIdentifier(), request.getPageNum(), request.getPageSize());

    }

    @Override
    public List<DeviceProperty> getPropertyHistory(String deviceId, String name, long start, long end, int size) {
        return devicePropertyData.findDevicePropertyHistory(deviceId, name, start, end, size);
    }

    @Override
    public boolean unbindDevice(String deviceId) {

        DeviceInfo deviceInfo = deviceInfoData.findByDeviceId(deviceId);
        dataOwnerService.checkOwner(deviceInfo);
        deviceCtrlService.unbindDevice(deviceId);
        return true;
    }

    @Override
    public boolean addTag(DeviceTagAddBo bo) {
        String deviceId = bo.getDeviceId();
        DeviceInfo device = deviceInfoData.findByDeviceId(deviceId);
        dataOwnerService.checkOwner(device);
        deviceInfoData.updateTag(deviceId, bo.to(DeviceInfo.Tag.class));
        return true;
    }

    @Override
    public boolean simulateSend(ThingModelMessage message) {
        DeviceInfo device = deviceInfoData.findByDeviceId(message.getDeviceId());
        dataOwnerService.checkOwner(device);

        message.setMid(UniqueIdUtil.newRequestId());
        message.setOccurred(System.currentTimeMillis());
        message.setTime(System.currentTimeMillis());
        producer.publish(Constants.THING_MODEL_MESSAGE_TOPIC, message);
        return true;
    }

    @Override
    public DeferredResult addConsumer(String deviceId, String clientId) {
    //修改为通过websocket下发给app
        DeferredResult<ThingModelMessage> result = new DeferredResult<>(0L);
        String uid = AuthUtil.getUserId();
        DeviceInfo deviceInfo = deviceInfoData.findByDeviceId(deviceId);
        dataOwnerService.checkOwner(deviceInfo);

//        按用户+客户端ID订阅
        return deferredDataConsumer.newConsumer(uid + clientId,
                Constants.HTTP_CONSUMER_DEVICE_INFO_TOPIC + deviceId);
    }

    @Override
    public Paging<DeviceGroupVo> selectGroupPageList(PageRequest<DeviceGroupBo> pageRequest) {
        DeviceGroupBo data = pageRequest.getData();
        return deviceGroupData.findByNameLike(data.getName(), pageRequest.getPageNum(), pageRequest.getPageSize()).to(DeviceGroupVo.class);
    }

    @Override
    public boolean addGroup(DeviceGroup group) {
        group.setUid(AuthUtil.getUserId());
        if (deviceGroupData.findById(group.getId()) != null) {
            throw new BizException(ErrCode.GROUP_ALREADY);
        }
        deviceGroupData.save(group);
        return true;
    }

    @Override
    public boolean updateGroup(DeviceGroupBo bo) {
        DeviceGroup group = bo.to(DeviceGroup.class);
        DeviceGroup dbGroup = deviceGroupData.findById(group.getId());
        if (dbGroup == null) {
            // 如果不存在，则添加分组
            return addGroup(group);
//            throw new BizException(ErrCode.GROUP_NOT_FOUND);
        }
        dataOwnerService.checkOwner(dbGroup);
        ReflectUtil.copyNoNulls(group, dbGroup);

        deviceGroupData.save(dbGroup);
        //更新设备中的组信息
        deviceInfoData.updateGroup(dbGroup.getId(), new DeviceInfo.Group(dbGroup.getId(), dbGroup.getName()));
        return true;
    }

    @Override
    public boolean deleteGroup(String id) {
        DeviceGroup group = deviceGroupData.findById(id);
        if (group == null) {
            throw new BizException(ErrCode.GROUP_NOT_FOUND);
        }
        dataOwnerService.checkOwner(group);
        //删除分组
        deviceGroupData.deleteById(id);

        //移除设备信息中的分组
        deviceInfoData.removeGroup(group.getId());
        return true;
    }

    @Override
    public boolean clearGroup(String id) {
        DeviceGroup group = deviceGroupData.findById(id);
        if (group == null) {
            throw new BizException(ErrCode.GROUP_NOT_FOUND);
        }
        dataOwnerService.checkOwner(group);

        //设备数量清零
        group.setDeviceQty(0);
        deviceGroupData.save(group);

        //移除设备信息中的分组
        deviceInfoData.removeGroup(group.getId());
        return true;
    }

    @Override
    public boolean addDevice2Group(DeviceAddGroupBo data) {

        String group = data.getGroup();
        List<String> devices = data.getDevices();
        DeviceGroup deviceGroup = deviceGroupData.findById(group);
        if (deviceGroup == null) {
            throw new BizException(ErrCode.GROUP_NOT_FOUND);
        }
        dataOwnerService.checkOwner(deviceGroup);

        for (String device : devices) {
            DeviceInfo deviceInfo = deviceInfoData.findByDeviceId(device);
            if (deviceInfo == null) {
                continue;
            }

            dataOwnerService.checkOwner(deviceInfo);
            //添加设备到组
            deviceInfoData.addToGroup(device, new DeviceInfo.Group(group, deviceGroup.getName()));
        }
        //统计组下设备数量
        long qty = deviceInfoData.countByGroupId(group);
        //更新组信息
        deviceGroup.setDeviceQty((int) qty);
        deviceGroupData.save(deviceGroup);
        return true;
    }

    @Override
    public boolean removeDevices(String group, List<String> devices) {

        DeviceGroup deviceGroup = deviceGroupData.findById(group);
        if (deviceGroup == null) {
            throw new BizException(ErrCode.GROUP_NOT_FOUND);
        }
        dataOwnerService.checkOwner(deviceGroup);

        for (String device : devices) {
            DeviceInfo deviceInfo = deviceInfoData.findByDeviceId(device);
            if (deviceInfo == null) {
                continue;
            }

            dataOwnerService.checkOwner(deviceInfo);
            //删除设备所在组
            deviceInfoData.removeGroup(device, group);
        }
        //统计组下设备数量
        long qty = deviceInfoData.countByGroupId(group);
        //更新组信息
        deviceGroup.setDeviceQty((int) qty);
        deviceGroupData.save(deviceGroup);
        return true;
    }

    @Override
    public boolean saveConfig(DeviceConfig data) {
        String deviceId = data.getDeviceId();
        String config = data.getConfig();
        DeviceInfo deviceInfo = deviceInfoData.findByDeviceId(deviceId);
        dataOwnerService.checkOwner(deviceInfo);

        DeviceConfig deviceConfig = deviceConfigData.findByDeviceId(deviceId);
        if (deviceConfig == null) {
            deviceConfig = DeviceConfig.builder()
                    .deviceId(deviceId)
                    .deviceName(deviceInfo.getDeviceName())
                    .productKey(deviceInfo.getProductKey())
                    .config(config)
                    .createAt(System.currentTimeMillis())
                    .build();
        } else {
            deviceConfig.setConfig(config);
        }

        deviceConfigData.save(deviceConfig);
        return true;
    }

    @Override
    public DeviceConfigVo getConfig(String deviceId) {
        DeviceInfo deviceInfo = deviceInfoData.findByDeviceId(deviceId);
        dataOwnerService.checkOwner(deviceInfo);
        return MapstructUtils.convert(deviceConfigData.findByDeviceId(deviceId), DeviceConfigVo.class);

    }

    @Override
    public boolean saveDevice(DeviceInfoBo data) {
        DeviceInfo di = data.to(DeviceInfo.class);
        di.setLocate(new DeviceInfo.Locate(data.getLongitude(), data.getLatitude()));
        if (StringUtils.isBlank(data.getSecret())) {
            data.setSecret(RandomStringUtils.random(16));
        }
        //deviceName不可重复
        DeviceInfo deviceRepetition = deviceInfoData.findByDeviceName(data.getDeviceName());
        if (deviceRepetition != null && !deviceRepetition.getDeviceId().equals(di.getDeviceId())) {
            throw new BizException(ErrCode.MODEL_DEVICE_ALREADY);
        }
        return deviceInfoData.save(di) != null;
    }


    @SneakyThrows
    @Override
    public String importGroup(MultipartFile file) {
        ExcelResult<DeviceGroupImportVo> result = ExcelUtil.importExcel(file.getInputStream(), DeviceGroupImportVo.class, new DeviceGroupImportListener(true));
        return result.getAnalysis();
    }

    @Override
    public DeviceGroupVo getDeviceGroup(String id) {
        DeviceGroup deviceGroup = deviceGroupData.findById(id);
        return MapstructUtils.convert(deviceGroup, DeviceGroupVo.class);
    }

    @SneakyThrows
    @Override
    public String importDevice(MultipartFile file) {
        ExcelResult<DeviceInfoImportVo> result = ExcelUtil.importExcel(file.getInputStream(), DeviceInfoImportVo.class, new DeviceInfoImportListener(true));
        return result.getAnalysis();
    }


}
