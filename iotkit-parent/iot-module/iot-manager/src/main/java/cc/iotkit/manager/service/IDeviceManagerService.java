package cc.iotkit.manager.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.common.thing.ThingModelMessage;
import cc.iotkit.manager.dto.bo.device.DeviceInfoBo;
import cc.iotkit.manager.dto.bo.device.DeviceLogQueryBo;
import cc.iotkit.manager.dto.bo.device.DeviceQueryBo;
import cc.iotkit.manager.dto.bo.device.DeviceTagAddBo;
import cc.iotkit.manager.dto.bo.devicegroup.DeviceAddGroupBo;
import cc.iotkit.manager.dto.bo.devicegroup.DeviceGroupBo;
import cc.iotkit.manager.dto.vo.deviceconfig.DeviceConfigVo;
import cc.iotkit.manager.dto.vo.devicegroup.DeviceGroupVo;
import cc.iotkit.manager.dto.vo.deviceinfo.DeviceInfoVo;
import cc.iotkit.manager.dto.vo.deviceinfo.ParentDeviceVo;
import cc.iotkit.model.device.DeviceConfig;
import cc.iotkit.model.device.DeviceGroup;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.device.message.DeviceProperty;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author: jay
 * @Date: 2023/5/31 11:05
 * @Version: V1.0
 * @Description: 设备服务接口
 */
public interface IDeviceManagerService {
    Paging<DeviceInfoVo> getDevices(PageRequest<DeviceQueryBo> pageRequest);

    boolean addDevice(DeviceInfoBo data);

    List<DeviceInfoVo> selectChildrenPageList(String deviceId);

    List<ParentDeviceVo> getParentDevices();

    DeviceInfo getDetail(String deviceId);

    DeviceInfo getByPkDn(String pk, String dn);

    boolean deleteDevice(String data);

    boolean batchDeleteDevice(List<String> ids);

    Paging<ThingModelMessage> logs(PageRequest<DeviceLogQueryBo> request);

    List<DeviceProperty> getPropertyHistory(String deviceId, String name, long start, long end,int size);

    boolean unbindDevice(String data);

    boolean addTag(DeviceTagAddBo bo);

    boolean simulateSend(ThingModelMessage message);

    DeferredResult addConsumer(String deviceId, String clientId);

    Paging<DeviceGroupVo> selectGroupPageList(PageRequest<DeviceGroupBo> pageRequest);

    boolean addGroup(DeviceGroup group);

    boolean updateGroup(DeviceGroupBo data);

    boolean deleteGroup(String id);

    boolean clearGroup(String id);

    boolean addDevice2Group(DeviceAddGroupBo data);

    boolean removeDevices(String group, List<String> devices);

    boolean saveConfig(DeviceConfig data);

    DeviceConfigVo getConfig(String deviceId);

    boolean saveDevice(DeviceInfoBo data);

    String importGroup(MultipartFile file);

    DeviceGroupVo getDeviceGroup(String id);

    String importDevice(MultipartFile file);
}
