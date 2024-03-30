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

import cc.iotkit.data.model.TbVirtualDevice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VirtualDeviceRepository extends JpaRepository<TbVirtualDevice, String> {

    List<TbVirtualDevice> findByUid(String uid);

    Page<TbVirtualDevice> findByUid(String uid, Pageable pageable);

    List<TbVirtualDevice> findByTriggerAndState(String trigger, String state);

}
