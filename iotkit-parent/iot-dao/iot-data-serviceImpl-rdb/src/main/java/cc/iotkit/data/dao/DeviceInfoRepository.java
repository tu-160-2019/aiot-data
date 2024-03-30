package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbDeviceInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface DeviceInfoRepository extends JpaRepository<TbDeviceInfo, String>, QuerydslPredicateExecutor<TbDeviceInfo> {

    TbDeviceInfo findByDeviceId(String deviceId);

    List<TbDeviceInfo> findByParentId(String parentId);

    TbDeviceInfo findByDeviceName(String deviceName);

}
