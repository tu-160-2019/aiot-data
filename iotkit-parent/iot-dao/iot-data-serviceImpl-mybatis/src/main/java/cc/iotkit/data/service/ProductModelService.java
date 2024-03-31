package cc.iotkit.data.service;

import cc.iotkit.data.model.TbProductModel;
import cc.iotkit.model.product.ProductModel;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ProductModelService extends IService<TbProductModel> {

    ProductModel findByModel(String model);

    List<ProductModel> findByProductKey(String productKey);
}
