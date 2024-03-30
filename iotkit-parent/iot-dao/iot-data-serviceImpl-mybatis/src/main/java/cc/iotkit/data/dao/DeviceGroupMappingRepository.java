package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbDeviceGroupMapping;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DeviceGroupMappingRepository extends BaseMapper<TbDeviceGroupMapping> {

    List<TbDeviceGroupMapping> findByDeviceId(String deviceId);

    TbDeviceGroupMapping findByDeviceIdAndGroupId(String deviceId, String groupId);

    long countByGroupId(String groupId);

}
