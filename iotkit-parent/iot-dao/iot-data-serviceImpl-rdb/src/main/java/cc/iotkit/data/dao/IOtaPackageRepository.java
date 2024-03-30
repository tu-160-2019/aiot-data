package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbOtaPackage;
import cc.iotkit.model.ota.OtaPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @Author: 石恒
 * @Date: 2023/5/19 21:54
 * @Description:
 */
public interface IOtaPackageRepository extends JpaRepository<TbOtaPackage, String> {
    List<OtaPackage> findByVersionGreaterThan(String version);
}
