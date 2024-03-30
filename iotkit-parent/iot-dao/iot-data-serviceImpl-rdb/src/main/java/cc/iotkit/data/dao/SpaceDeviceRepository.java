/*
 * +----------------------------------------------------------------------
 * | Copyright (c) 奇特物联 2021-2022 All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed 未经许可不能去掉「奇特物联」相关版权
 * +----------------------------------------------------------------------
 * | Author: xw2sy@163.com
 * +----------------------------------------------------------------------
 */
package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbSpaceDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpaceDeviceRepository extends JpaRepository<TbSpaceDevice, Long> {

    List<TbSpaceDevice> findByHomeIdAndCollect(Long homeId,boolean collect);

    TbSpaceDevice findByDeviceId(String deviceId);

    List<TbSpaceDevice> findByHomeId(Long homeId);

    List<TbSpaceDevice> findBySpaceId(Long spaceId);

    void deleteAllBySpaceId(Long spaceId);
}
