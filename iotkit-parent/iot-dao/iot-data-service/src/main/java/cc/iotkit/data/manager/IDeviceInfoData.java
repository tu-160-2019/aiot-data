/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.data.manager;

import cc.iotkit.common.api.Paging;
import cc.iotkit.data.IOwnedData;
import cc.iotkit.model.device.DeviceInfo;
import cc.iotkit.model.device.message.DevicePropertyCache;
import cc.iotkit.model.stats.DataItem;

import java.util.List;
import java.util.Map;

public interface IDeviceInfoData extends IOwnedData<DeviceInfo, String> {

    /**
     * 保存设备属性
     *
     * @param deviceId   设备id
     * @param properties 设备属性map
     */
    void saveProperties(String deviceId, Map<String, DevicePropertyCache> properties);

    /**
     * 获取设备属性map
     *
     * @param deviceId 设备id
     */
    Map<String, DevicePropertyCache> getProperties(String deviceId);

    /**
     * 获取设备属性更新时间
     *
     * @param deviceId 设备id
     * @return timestamp
     */
    long getPropertyUpdateTime(String deviceId);

    /**
     * 根据设备ID取设备信息
     *
     * @param deviceId 设备ID
     */
    DeviceInfo findByDeviceId(String deviceId);

    /**
     * 根据父设备ID取子设备列表
     *
     * @param parentId 父设备ID
     */
    List<DeviceInfo> findByParentId(String parentId);

    /**
     * 根据父设备ID取子设备ID列表
     *
     * @param parentId 父设备ID
     */
    List<String> findSubDeviceIds(String parentId);

    /**
     * 根据deviceName查找设备
     */
    DeviceInfo findByDeviceName(String deviceName);

    /**
     * 按条件搜索设备
     *
     * @param uid        所属账号id
     * @param subUid     子账号id
     * @param productKey 产品key
     * @param groupId    设备分组
     * @param online     是否在线:true在线,false离线
     * @param keyword    关键字
     * @param page       页码
     * @param size       分页大小
     */
    Paging<DeviceInfo> findByConditions(String uid, String subUid, String productKey,
                                        String groupId, Boolean online, String keyword,
                                        int page, int size);

    /**
     * 更新设备标签
     *
     * @param deviceId 设备ID
     * @param tag      标签
     */
    void updateTag(String deviceId, DeviceInfo.Tag tag);

    /**
     * 获取按品类统计的用户设备数
     */
    List<DataItem> getDeviceStatsByCategory(String uid);

    /**
     * 按分组id统计设备数量
     */
    long countByGroupId(String groupId);

    /**
     * 将设备添加到分组
     *
     * @param deviceId 设备ID
     * @param group    设备分组
     */
    void addToGroup(String deviceId, DeviceInfo.Group group);

    /**
     * 按组id更新设备分组
     *
     * @param groupId 分组ID
     * @param group   设备分组信息
     */
    void updateGroup(String groupId, DeviceInfo.Group group);

    /**
     * 移除指定设备信息中的分组
     *
     * @param deviceId 设备ID
     * @param groupId  分组ID
     */
    void removeGroup(String deviceId, String groupId);

    /**
     * 移除设备信息中的分组
     *
     * @param groupId 分组ID
     */
    void removeGroup(String groupId);

    /**
     * 获取所有网关类型设备
     *
     * @return
     */
    List<DeviceInfo> findByProductNodeType(String uid);

    /**
     * 是否存在product类型的设备
     *
     * @param productKey
     * @return
     */
    boolean existByProductKey(String productKey);

    /**
     * 查找待激活设备
     * @return
     */
    List<DeviceInfo> findNeverUsedDevices();
}
