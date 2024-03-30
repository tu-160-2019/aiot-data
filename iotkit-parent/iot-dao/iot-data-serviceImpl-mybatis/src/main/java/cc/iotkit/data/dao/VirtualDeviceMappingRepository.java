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

import cc.iotkit.data.model.TbVirtualDeviceMapping;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import javax.transaction.Transactional;
import java.util.List;

@Mapper
public interface VirtualDeviceMappingRepository extends BaseMapper<TbVirtualDeviceMapping> {

    List<TbVirtualDeviceMapping> findByVirtualId(String virtualId);

    @Transactional
    void deleteByVirtualId(String virtualId);

}
