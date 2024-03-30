/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.data.service;

import cc.iotkit.common.api.PageRequest;
import cc.iotkit.common.api.Paging;
import cc.iotkit.data.manager.IDeviceInfoData;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.device.message.DevicePropertyCache;
import cc.iotkit.model.stats.DataItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 包含设备属性的设备信息缓存服务
 */
@Service
@Qualifier("deviceInfoPropertyDataCache")
public class DeviceInfoPropertyDataCache implements IDeviceInfoData {

    @Autowired
    @Qualifier("deviceInfoDataCache")
    private IDeviceInfoData deviceInfoData;


    @Override
    public DeviceInfo findById(String s) {
        return deviceInfoData.findById(s);
    }

    @Override
    public List<DeviceInfo> findByIds(Collection<String> id) {
        return null;
    }

    @Override
    public DeviceInfo save(DeviceInfo data) {
        return deviceInfoData.save(data);
    }

    @Override
    public void batchSave(List<DeviceInfo> data) {

    }

    @Override
    public void deleteById(String s) {
        deviceInfoData.deleteById(s);
    }

    @Override
    public void deleteByIds(Collection<String> ids) {
        deviceInfoData.deleteByIds(ids);
    }

    @Override
    public long count() {
        return deviceInfoData.count();
    }

    @Override
    public List<DeviceInfo> findAll() {
        return deviceInfoData.findAll();
    }

    @Override
    public Paging<DeviceInfo> findAll(PageRequest<DeviceInfo> pageRequest) {
        return deviceInfoData.findAll(pageRequest);
    }

    @Override
    public List<DeviceInfo> findAllByCondition(DeviceInfo data) {
        return null;
    }

    @Override
    public DeviceInfo findOneByCondition(DeviceInfo data) {
        return null;
    }

    @Override
    public void saveProperties(String deviceId, Map<String, DevicePropertyCache> properties) {
        deviceInfoData.saveProperties(deviceId, properties);
    }

    @Override
    public Map<String, DevicePropertyCache> getProperties(String deviceId) {
        return deviceInfoData.getProperties(deviceId);
    }

    @Override
    public long getPropertyUpdateTime(String deviceId) {
        return deviceInfoData.getPropertyUpdateTime(deviceId);
    }

    @Override
    public DeviceInfo findByDeviceId(String deviceId) {
        DeviceInfo deviceInfo = deviceInfoData.findByDeviceId(deviceId);
        deviceInfo.setProperty(getProperties(deviceId));
        return deviceInfo;
    }

    @Override
    public DeviceInfo findByDeviceName(String deviceName) {
        DeviceInfo deviceInfo = deviceInfoData.findByDeviceName(deviceName);
        if (deviceInfo == null) {
            return null;
        }
        deviceInfo.setProperty(getProperties(deviceInfo.getDeviceId()));
        return deviceInfo;
    }

    @Override
    public List<DeviceInfo> findByParentId(String parentId) {
        return deviceInfoData.findByParentId(parentId);
    }

    @Override
    public List<String> findSubDeviceIds(String parentId) {
        return deviceInfoData.findSubDeviceIds(parentId);
    }

    @Override
    public Paging<DeviceInfo> findByConditions(String uid, String subUid, String productKey, String groupId, Boolean online, String keyword, int page, int size) {
        return deviceInfoData.findByConditions(uid, subUid, productKey, groupId, online, keyword, page, size);
    }

    @Override
    public void updateTag(String deviceId, DeviceInfo.Tag tag) {
        deviceInfoData.updateTag(deviceId, tag);
    }

    @Override
    public List<DataItem> getDeviceStatsByCategory(String uid) {
        return deviceInfoData.getDeviceStatsByCategory(uid);
    }

    @Override
    public long countByGroupId(String groupId) {
        return deviceInfoData.countByGroupId(groupId);
    }

    @Override
    public void addToGroup(String deviceId, DeviceInfo.Group group) {
        deviceInfoData.addToGroup(deviceId, group);
    }

    @Override
    public void updateGroup(String groupId, DeviceInfo.Group group) {
        deviceInfoData.updateGroup(groupId, group);
    }

    @Override
    public void removeGroup(String deviceId, String groupId) {
        deviceInfoData.removeGroup(deviceId, groupId);
    }

    @Override
    public void removeGroup(String groupId) {
        deviceInfoData.removeGroup(groupId);
    }

    @Override
    public List<DeviceInfo> findByProductNodeType(String uid) {
        return deviceInfoData.findByProductNodeType(uid);
    }

    @Override
    public boolean existByProductKey(String productKey) {
        return deviceInfoData.existByProductKey(productKey);
    }

    @Override
    public List<DeviceInfo> findNeverUsedDevices() {
        return deviceInfoData.findNeverUsedDevices();
    }

    @Override
    public List<DeviceInfo> findByUid(String uid) {
        return deviceInfoData.findByUid(uid);
    }

    @Override
    public Paging<DeviceInfo> findByUid(String uid, int page, int size) {
        return deviceInfoData.findByUid(uid, page, size);
    }

    @Override
    public long countByUid(String uid) {
        return deviceInfoData.countByUid(uid);
    }
}
