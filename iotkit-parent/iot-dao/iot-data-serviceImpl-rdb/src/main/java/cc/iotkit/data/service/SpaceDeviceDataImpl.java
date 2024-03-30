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

import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.dao.SpaceDeviceRepository;
import cc.iotkit.data.manager.ISpaceDeviceData;
import cc.iotkit.data.model.TbSpaceDevice;
import cc.iotkit.model.space.SpaceDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Primary
@Service
public class SpaceDeviceDataImpl implements ISpaceDeviceData, IJPACommData<SpaceDevice, Long> {

    @Autowired
    private SpaceDeviceRepository spaceDeviceRepository;


    @Override
    public JpaRepository getBaseRepository() {
        return spaceDeviceRepository;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbSpaceDevice.class;
    }

    @Override
    public Class getTClass() {
        return SpaceDevice.class;
    }


    @Override
    public List<SpaceDevice> findByHomeIdAndCollect(Long homeId, boolean collect) {
        return MapstructUtils.convert(spaceDeviceRepository.findByHomeIdAndCollect(homeId, collect), SpaceDevice.class);
    }

    @Override
    public List<SpaceDevice> findByHomeId(Long homeId) {
        return MapstructUtils.convert(spaceDeviceRepository.findByHomeId(homeId), SpaceDevice.class);
    }

    @Override
    public List<SpaceDevice> findBySpaceId(Long spaceId) {
        return MapstructUtils.convert(spaceDeviceRepository.findBySpaceId(spaceId), SpaceDevice.class);
    }

    @Override
    public SpaceDevice findByDeviceId(String deviceId) {
        return MapstructUtils.convert(spaceDeviceRepository.findByDeviceId(deviceId), SpaceDevice.class);
    }

    @Override
    public void deleteAllBySpaceId(Long spaceId) {
        spaceDeviceRepository.deleteAllBySpaceId(spaceId);
    }

}
