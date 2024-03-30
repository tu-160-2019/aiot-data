package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbDeviceSubUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceSubUserRepository extends JpaRepository<TbDeviceSubUser, String> {

    List<TbDeviceSubUser> findByDeviceId(String deviceId);

}
