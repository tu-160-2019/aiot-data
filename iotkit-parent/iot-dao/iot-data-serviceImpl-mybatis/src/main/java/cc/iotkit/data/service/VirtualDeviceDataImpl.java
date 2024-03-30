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

import cc.iotkit.data.dao.IJPACommData;
import cc.iotkit.data.manager.IVirtualDeviceData;
import cc.iotkit.data.dao.VirtualDeviceMappingRepository;
import cc.iotkit.data.dao.VirtualDeviceRepository;
import cc.iotkit.data.model.TbVirtualDevice;
import cc.iotkit.data.model.TbVirtualDeviceMapping;
import cc.iotkit.common.utils.MapstructUtils;
import cc.iotkit.common.api.Paging;
import cc.iotkit.model.device.VirtualDevice;
import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Primary
@Service
@RequiredArgsConstructor
public class VirtualDeviceDataImpl implements IVirtualDeviceData, IJPACommData<VirtualDevice, String> {

    private final VirtualDeviceRepository virtualDeviceRepository;

    private final VirtualDeviceMappingRepository virtualDeviceMappingRepository;

    @Override
    public JpaRepository getBaseRepository() {
        return virtualDeviceRepository;
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
        return MapstructUtils.convert(virtualDeviceRepository.findByUid(uid), VirtualDevice.class);
    }

    @Override
    public Paging<VirtualDevice> findByUid(String uid, int size, int page) {
        Page<TbVirtualDevice> paged = virtualDeviceRepository.findByUid(uid,
                Pageable.ofSize(size).withPage(page - 1));
        return new Paging<>(paged.getTotalElements(),
                MapstructUtils.convert(paged.getContent(), VirtualDevice.class));
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
        List<VirtualDevice> list = MapstructUtils.convert(virtualDeviceRepository
                .findByTriggerAndState(trigger, state), VirtualDevice.class);
        for (VirtualDevice virtualDevice : list) {
            virtualDevice.setDevices(getVirtualDeviceIds(virtualDevice.getId()));
        }
        return list;
    }

    @Override
    public VirtualDevice findById(String s) {
        VirtualDevice dto = MapstructUtils.convert(virtualDeviceRepository.findById(s).orElse(null), VirtualDevice.class);
        dto.setDevices(getVirtualDeviceIds(s));
        return dto;
    }


    private List<String> getVirtualDeviceIds(String virtualId) {
        List<TbVirtualDeviceMapping> deviceMappings = virtualDeviceMappingRepository.findByVirtualId(virtualId);
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
        virtualDeviceRepository.save(MapstructUtils.convert(data, TbVirtualDevice.class));

        //删除旧的添加新的关联设备记录
        virtualDeviceMappingRepository.deleteByVirtualId(data.getId());
        virtualDeviceMappingRepository.saveAllAndFlush(
                data.getDevices().stream().map(d -> new TbVirtualDeviceMapping(
                        IdUtil.simpleUUID(),
                        data.getId(),
                        d
                )).collect(Collectors.toList())
        );
        return data;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String s) {
        virtualDeviceRepository.deleteById(s);
        virtualDeviceMappingRepository.deleteByVirtualId(s);
    }


    @Override
    public List<VirtualDevice> findAll() {
        return MapstructUtils.convert(virtualDeviceRepository.findAll(), VirtualDevice.class);
    }


}
