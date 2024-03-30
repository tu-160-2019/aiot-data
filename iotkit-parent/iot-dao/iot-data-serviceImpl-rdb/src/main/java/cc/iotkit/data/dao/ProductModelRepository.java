package cc.iotkit.data.dao;

import cc.iotkit.data.model.TbProductModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductModelRepository extends JpaRepository<TbProductModel, String> {

    TbProductModel findByModel(String model);

    List<TbProductModel> findByProductKey(String productKey);

}
