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

import cc.iotkit.data.ICommonData;
import cc.iotkit.model.space.SpaceDevice;

import java.util.List;

public interface ISpaceDeviceData extends ICommonData<SpaceDevice, Long> {

    List<SpaceDevice> findByHomeIdAndCollect(Long homeId,boolean collect);

    List<SpaceDevice> findByHomeId(Long homeId);

    List<SpaceDevice> findBySpaceId(Long spaceId);

    SpaceDevice findByDeviceId(String deviceId);

    void deleteAllBySpaceId(Long spaceId);
}
