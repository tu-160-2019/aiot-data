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

import cc.iotkit.data.mapper.IJPACommData;
import cc.iotkit.data.manager.IVirtualDeviceData;

import cc.iotkit.data.model.TbVirtualDevice;
import cc.iotkit.data.model.TbVirtualDeviceMapping;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.api.Paging;
import cc.iotkit.model.device.VirtualDevice;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Primary
@Service
@RequiredArgsConstructor
public class VirtualDeviceDataImpl implements IVirtualDeviceData, IJPACommData<VirtualDevice, String> {
//public class VirtualDeviceDataImpl implements IVirtualDeviceData, IJPACommData<VirtualDevice, String, TbVirtualDevice> {

    private final VirtualDeviceService virtualDeviceService;

    private final VirtualDeviceMappingService virtualDeviceMappingService;

    private final SqlSessionFactory sqlSessionFactory;

    @Override
    public VirtualDeviceService getBaseRepository() {
        return virtualDeviceService;
    }

    @Override
    public Class getJpaRepositoryClass() {
        return TbVirtualDevice.class;
    }

    @Override
    public Class getTClass() {
        return VirtualDevice.class;
    }

    @Override
    public List<VirtualDevice> findByUid(String uid) {
        return MapstructUtils.convert(virtualDeviceService.list(new LambdaQueryWrapper<TbVirtualDevice>()
                .eq(TbVirtualDevice::getUid, uid)), VirtualDevice.class);
    }

    @Override
    public Paging<VirtualDevice> findByUid(String uid, int size, int page) {
        Page<TbVirtualDevice> virtualDevicePage = virtualDeviceService.findByUid(uid, size, page);
        return new Paging<>(virtualDevicePage.getTotal(),
                MapstructUtils.convert(virtualDevicePage.getRecords(), VirtualDevice.class));
    }

    @Override
    public long countByUid(String uid) {
        return 0;
    }

    @Override
    public List<VirtualDevice> findByUidAndState(String uid, String state) {
        return null;
    }

    @Override
    public List<VirtualDevice> findByTriggerAndState(String trigger, String state) {
        List<VirtualDevice> list = MapstructUtils.convert(virtualDeviceService.findByTriggerAndState(trigger, state),
                VirtualDevice.class);
        for (VirtualDevice virtualDevice : list) {
            virtualDevice.setDevices(getVirtualDeviceIds(virtualDevice.getId()));
        }
        return list;
    }

    @Override
    public VirtualDevice findById(String s) {
        VirtualDevice dto = MapstructUtils.convert(Optional.of(virtualDeviceService.getById(s)).orElse(null),
                VirtualDevice.class);
        if (Objects.isNull(dto)) {
            return null;
        }
        dto.setDevices(getVirtualDeviceIds(s));
        return dto;
    }


    private List<String> getVirtualDeviceIds(String virtualId) {
        List<TbVirtualDeviceMapping> deviceMappings = virtualDeviceMappingService.listByVirtualId(virtualId);
        return deviceMappings.stream().map(TbVirtualDeviceMapping::getDeviceId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VirtualDevice save(VirtualDevice data) {
        if (StringUtils.isBlank(data.getId())) {
            data.setId(IdUtil.simpleUUID());
            data.setState(VirtualDevice.STATE_STOPPED);
            data.setCreateAt(System.currentTimeMillis());
        }
        virtualDeviceService.saveOrUpdate(MapstructUtils.convert(data, TbVirtualDevice.class));

        //删除旧的添加新的关联设备记录
        virtualDeviceMappingService.deleteByVirtualId(data.getId());
        try (SqlSession session = sqlSessionFactory.openSession()) {
            virtualDeviceMappingService.saveBatch(data.getDevices().stream().map(d -> new TbVirtualDeviceMapping(
                    IdUtil.simpleUUID(),
                    data.getId(),
                    d
            )).collect(Collectors.toList()));
            session.commit(); // 刷新更改到数据库
        }
        return data;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String s) {
        virtualDeviceService.removeById(s);
        virtualDeviceMappingService.deleteByVirtualId(s);
    }


    @Override
    public List<VirtualDevice> findAll() {
        return MapstructUtils.convert(virtualDeviceService.list(), VirtualDevice.class);
    }


}
