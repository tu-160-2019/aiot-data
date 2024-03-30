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
import cc.iotkit.common.constant.Constants;
import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.data.cache.DeviceInfoCacheEvict;
import cc.iotkit.data.cache.DeviceInfoCachePut;
import cc.iotkit.data.manager.IDeviceInfoData;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.device.message.DevicePropertyCache;
import cc.iotkit.model.stats.DataItem;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Qualifier("deviceInfoDataCache")
public class DeviceInfoDataCache implements IDeviceInfoData, SmartInitializingSingleton {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private DeviceInfoCachePut deviceInfoCachePut;
    @Autowired
    private DeviceInfoCacheEvict deviceInfoCacheEvict;

    @Autowired
    private IDeviceInfoData deviceInfoData;


    @Override
    public void afterSingletonsInstantiated() {
        if ("true".equals(System.getProperty("disabledEmbeddedRedis"))) {
            return;
        }

        //需要等待缓存初始化
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //内嵌redis每次启动需要重新装载缓存
                int page = 1;
                Paging<DeviceInfo> paged;
                List<String> parentIds = new ArrayList<>();
                PageRequest<DeviceInfo> pageRequest = new PageRequest<>();
                pageRequest.setPageSize(1000);
                pageRequest.setPageNum(page);

                while (!(paged = deviceInfoData.findAll(pageRequest)).getRows().isEmpty()) {
                    pageRequest.setPageNum(page++);
                    List<DeviceInfo> devices = paged.getRows();
                    for (DeviceInfo device : devices) {
                        //装载设备信息缓存
                        deviceInfoCachePut.findByDeviceId(device.getDeviceId(), device);
                        deviceInfoCachePut.findByDeviceName(device.getDeviceName(), device);
                        String parentId = device.getParentId();
                        if (StringUtils.isBlank(parentId)) {
                            parentIds.add(parentId);
                        }
                    }
                }
                //装载子设备id列表缓存
                for (String parentId : parentIds) {
                    putSubDeviceIds(parentId);
                }
            }
        }, 100);
    }

    private String getPropertyCacheKey(String deviceId) {
        return String.format(Constants.PROPERTY_CACHE_KEY, deviceId);
    }

    @Override
    public void saveProperties(String deviceId, Map<String, DevicePropertyCache> properties) {
        Map<String, DevicePropertyCache> old = getProperties(deviceId);
        old.putAll(properties);
        redisTemplate.opsForValue().set(getPropertyCacheKey(deviceId),
                JsonUtils.toJsonString(new PropertyCacheInfo(System.currentTimeMillis(), old))
        );
    }

    /**
     * 清除设备属性缓存
     */
    private void clearProperties(String deviceId) {
        redisTemplate.delete(getPropertyCacheKey(deviceId));
    }

    @Override
    public Map<String, DevicePropertyCache> getProperties(String deviceId) {
        return getPropertyCacheInfo(deviceId).getProperties();
    }

    private PropertyCacheInfo getPropertyCacheInfo(String deviceId) {
        String json = redisTemplate.opsForValue().get(getPropertyCacheKey(deviceId));
        if (StringUtils.isBlank(json)) {
            return new PropertyCacheInfo(0, new HashMap<>());
        }
        return JsonUtils.parseObject(json, new TypeReference<>() {
        });
    }

    @Override
    public long getPropertyUpdateTime(String deviceId) {
        return getPropertyCacheInfo(deviceId).getUpdateTime();
    }

    @Override
    @Cacheable(value = Constants.CACHE_DEVICE_INFO, key = "#root.method.name+#deviceId", unless = "#result == null")
    public DeviceInfo findByDeviceId(String deviceId) {
        return deviceInfoData.findByDeviceId(deviceId);
    }

    @Override
    @Cacheable(value = Constants.CACHE_DEVICE_INFO, key = "#root.method.name+#deviceName", unless = "#result == null")
    public DeviceInfo findByDeviceName(String deviceName) {
        return deviceInfoData.findByDeviceName(deviceName);
    }

    @Override
    public List<DeviceInfo> findByParentId(String parentId) {
        return deviceInfoData.findByParentId(parentId);
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
    public List<String> findSubDeviceIds(String parentId) {
        return deviceInfoData.findSubDeviceIds(parentId);
    }

    @Override
    public Paging<DeviceInfo> findByConditions(String uid, String subUid, String productKey,
                                               String groupId, Boolean state, String keyword, int page, int size) {
        return deviceInfoData.findByConditions(uid, subUid, productKey, groupId, state, keyword, page, size);
    }

    @Override
    public void updateTag(String deviceId, DeviceInfo.Tag tag) {
        deviceInfoData.updateTag(deviceId, tag);
        DeviceInfo data = deviceInfoData.findByDeviceId(deviceId);
        putDeviceInfo(data);
    }

    @Override
    @Cacheable(value = Constants.CACHE_DEVICE_STATS, key = "#root.method.name+#uid", unless = "#result == null")
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
        DeviceInfo data = deviceInfoData.findByDeviceId(deviceId);
        putDeviceInfo(data);
    }

    @Override
    public void updateGroup(String groupId, DeviceInfo.Group group) {
        deviceInfoData.updateGroup(groupId, group);
    }

    @Override
    public void removeGroup(String deviceId, String groupId) {
        deviceInfoData.removeGroup(deviceId, groupId);
        DeviceInfo data = deviceInfoData.findByDeviceId(deviceId);
        putDeviceInfo(data);
    }

    @Override
    public void removeGroup(String groupId) {
        deviceInfoData.removeGroup(groupId);
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

    @Override
    public DeviceInfo findById(String s) {
        return deviceInfoData.findById(s);
    }

    @Override
    public List<DeviceInfo> findByIds(Collection<String> ids) {
        return deviceInfoData.findByIds(ids);
    }

    @Override
    public DeviceInfo save(DeviceInfo data) {
        DeviceInfo r = deviceInfoData.save(data);
        //更新设备信息缓存
        putDeviceInfo(data);
        //更新子设备列表缓存
        putSubDeviceIds(data.getParentId());
        return r;
    }

    @Override
    public void batchSave(List<DeviceInfo> data) {

    }

    @Override
    public void deleteById(String s) {
        DeviceInfo device = deviceInfoData.findById(s);
        deviceInfoData.deleteById(s);
        //清除缓存
        deviceInfoCacheEvict.findByDeviceId(device.getDeviceId());
        deviceInfoCacheEvict.findByDeviceName(device.getDeviceName());
        //清除属性缓存
        clearProperties(device.getDeviceId());
        //更新子设备列表缓存
        putSubDeviceIds(device.getParentId());
    }

    @Override
    public void deleteByIds(Collection<String> ids) {
        List<DeviceInfo> deviceInfos = deviceInfoData.findByIds(ids);
        deviceInfos.forEach(device -> {
            deviceInfoCacheEvict.findByDeviceId(device.getDeviceId());
            deviceInfoCacheEvict.findByDeviceName(device.getDeviceName());
            //清除属性缓存
            clearProperties(device.getDeviceId());
            //更新子设备列表缓存
            putSubDeviceIds(device.getParentId());
        });
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
        return deviceInfoData.findAllByCondition(data);
    }

    @Override
    public DeviceInfo findOneByCondition(DeviceInfo data) {
        return null;
    }

    /**
     * 更新缓存
     */
    private void putDeviceInfo(DeviceInfo data) {
        deviceInfoCachePut.findByDeviceId(data.getDeviceId(), data);
        deviceInfoCachePut.findByDeviceName(data.getDeviceName(), data);
    }

    /**
     * 更新子设备id列表
     */
    private void putSubDeviceIds(String parentId) {
        if (StringUtils.isBlank(parentId)) {
            return;
        }
        List<String> subDeviceIds = deviceInfoData.findSubDeviceIds(parentId);
        deviceInfoCachePut.findSubDeviceIds(parentId, subDeviceIds);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class PropertyCacheInfo {

        private long updateTime;

        private Map<String, DevicePropertyCache> properties;

    }
}
