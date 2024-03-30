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

import cc.iotkit.data.model.TbDeviceTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceTagRepository extends JpaRepository<TbDeviceTag, String> {

    List<TbDeviceTag> findByDeviceId(String deviceId);

    TbDeviceTag findByDeviceIdAndCode(String deviceId, String code);

}
