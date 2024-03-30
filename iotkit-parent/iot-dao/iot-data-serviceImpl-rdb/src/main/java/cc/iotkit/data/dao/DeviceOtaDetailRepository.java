package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbDeviceOtaInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

/**
 * @Author: 石恒
 * @Date: 2023/6/15 22:21
 * @Description:
 */
public interface DeviceOtaDetailRepository extends JpaRepository<TbDeviceOtaInfo, String>, QuerydslPredicateExecutor<TbDeviceOtaInfo> {
}
