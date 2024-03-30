package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbOtaDevice;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: 石恒
 * @Date: 2023/5/25 23:43
 * @Description:
 */
public interface IOtaDeviceRepository extends JpaRepository<TbOtaDevice, String> {
}
