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
import cc.iotkit.data.mapper.IJPACommData;

import cc.iotkit.data.manager.ISpaceDeviceData;
import cc.iotkit.data.model.TbSpaceDevice;
import cc.iotkit.model.space.SpaceDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Resource;
import java.util.List;

@Primary
@Service
public class SpaceDeviceDataImpl implements ISpaceDeviceData, IJPACommData<SpaceDevice, Long> {
//public class SpaceDeviceDataImpl implements ISpaceDeviceData, IJPACommData<SpaceDevice, Long, TbSpaceDevice> {

    @Resource
    @Qualifier("DBSpaceDeviceServiceImpl")
    private SpaceDeviceService dbspaceDeviceService;


    @Override
    public SpaceDeviceService getBaseRepository() {
        return dbspaceDeviceService;
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
        return dbspaceDeviceService.findByHomeIdAndCollect(homeId, collect);
    }

    @Override
    public List<SpaceDevice> findByHomeId(Long homeId) {
        return dbspaceDeviceService.findByHomeId(homeId);
    }

    @Override
    public List<SpaceDevice> findBySpaceId(Long spaceId) {
        return dbspaceDeviceService.findBySpaceId(spaceId);
    }

    @Override
    public SpaceDevice findByDeviceId(String deviceId) {
        return dbspaceDeviceService.findByDeviceId(deviceId);
    }

    @Override
    public void deleteAllBySpaceId(Long spaceId) {
        dbspaceDeviceService.deleteAllBySpaceId(spaceId);
    }

}
