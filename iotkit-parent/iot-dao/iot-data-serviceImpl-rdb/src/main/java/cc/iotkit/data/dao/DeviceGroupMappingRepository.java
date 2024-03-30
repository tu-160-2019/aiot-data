package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbDeviceGroupMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceGroupMappingRepository extends JpaRepository<TbDeviceGroupMapping, String> {

    List<TbDeviceGroupMapping> findByDeviceId(String deviceId);

    TbDeviceGroupMapping findByDeviceIdAndGroupId(String deviceId, String groupId);

    long countByGroupId(String groupId);

}
